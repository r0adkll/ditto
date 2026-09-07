# Releasing

Ditto publishes to Maven Central through the [Central Portal](https://central.sonatype.com), using
the Vanniktech publish plugin. Coordinates are `com.r0adkll.ditto:<module>`; the modules are
`ditto-core`, `ditto-components` and `ditto-material3-interop`.

Two things publish automatically:

| What | When | Task |
|---|---|---|
| Snapshot | every push to `main` that touches code | `publishToMavenCentral` |
| Release | pushing a `v*` tag | `publishAndReleaseToMavenCentral` |

## One-time setup

None of this can be done from the repo — it needs an account and a key.

**1. Verify the namespace.** In the Central Portal, add the namespace `com.r0adkll`. It verifies by
DNS: the portal gives you a token, and you add a TXT record on `r0adkll.com` (Cloudflare) whose
value is that token. Verification is usually minutes.

`com.r0adkll` is the right namespace because the domain is yours. If it ever stops being yours, the
alternative is `io.github.r0adkll`, verified by a repo instead — that changes the `GROUP` in
`gradle.properties` and every consumer's coordinates, but not the Kotlin package names.

**2. Generate a signing key.** Central requires released artifacts to be signed. Snapshots are not.

```bash
gpg --quick-generate-key "Drew Heavner <you@example.com>" rsa4096 sign 2y
gpg --list-secret-keys --keyid-format=long          # note the long key id
gpg --keyserver keyserver.ubuntu.com --send-keys <FULL_FINGERPRINT>
gpg --armor --export-secret-keys <KEY_ID> | pbcopy  # this is SIGNING_KEY
```

The key has to be on a public keyserver or validation fails. Export the **secret** key armored,
newlines and all — GitHub secrets keep them.

**3. Add the repository secrets** (Settings → Secrets and variables → Actions):

| Secret | Where it comes from |
|---|---|
| `MAVEN_CENTRAL_USERNAME` | Central Portal → Generate User Token (the token *username*, not your login) |
| `MAVEN_CENTRAL_PASSWORD` | the matching token password |
| `SIGNING_KEY` | the armored secret key from above |
| `SIGNING_KEY_ID` | the long key id |
| `SIGNING_PASSWORD` | the key's passphrase |

Until `MAVEN_CENTRAL_USERNAME` exists, the snapshot workflow skips with a warning rather than
failing every push.

## Cutting a release

`main` always carries the next `-SNAPSHOT`. A release is three commits' worth of bookkeeping:

```bash
# 1. Drop the suffix
sed -i '' 's/^VERSION_NAME=.*/VERSION_NAME=0.1.0/' gradle.properties
./gradlew apiCheck check                      # the API dumps are the compatibility contract
git commit -am "Release 0.1.0" && git push

# 2. Tag it — this is what publishes
git tag v0.1.0 && git push origin v0.1.0

# 3. Back to snapshots
sed -i '' 's/^VERSION_NAME=.*/VERSION_NAME=0.2.0-SNAPSHOT/' gradle.properties
git commit -am "Prepare 0.2.0-SNAPSHOT" && git push
```

The Publish workflow refuses to run if the tag and `VERSION_NAME` disagree, or if `VERSION_NAME` is
still a snapshot, so step 2 without step 1 fails loudly instead of publishing the wrong thing.

Artifacts appear in the Central Portal within a few minutes and on `repo1.maven.org` within an
hour or so.

## Verifying before you tag

`publishToMavenLocal` produces exactly what would be uploaded:

```bash
./gradlew publishToMavenLocal --no-configuration-cache
ls ~/.m2/repository/com/r0adkll/ditto/ditto-core/<version>/
```

You should see a `.jar`, `-sources.jar`, `-javadoc.jar` (Dokka HTML — the plugin picks it up
because the Dokka plugin is applied to published modules), `.pom` and `.module`, and one directory
per target: `-android`, `-jvm`, `-iosarm64`, `-iossimulatorarm64`, `-wasm-js`.

## What a release commits you to

The `.api` dumps in each module are the binary-compatibility contract, and `apiCheck` runs in CI.
Before 1.0 they are a change log rather than a promise: breaking changes are allowed, but they
should be deliberate and appear in the dump diff, not by accident.
