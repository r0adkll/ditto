#!/usr/bin/env node
/**
 * Two things the build cannot notice on its own:
 *
 * 1. **Demo ids are public API.** Docs pages embed `/catalog/?id=<id>`, so renaming one in Kotlin
 *    silently breaks a published page — the iframe just says no demo is registered.
 * 2. **The component sidebar is hand-grouped.** 33 components in one alphabetical list is a wall,
 *    so `astro.config.mjs` lists them by group; a new page that nobody adds there is reachable
 *    only by search.
 */
import { readFileSync, readdirSync, statSync } from 'node:fs';
import { join, dirname } from 'node:path';
import { fileURLToPath } from 'node:url';

const root = join(dirname(fileURLToPath(import.meta.url)), '..');
const registry = join(root, '..', 'catalog/shared/src/commonMain/kotlin/com/r0adkll/ditto/catalog/Demos.kt');

const declared = new Set(
  [...readFileSync(registry, 'utf8').matchAll(/DemoItem\(\s*"([a-z0-9-]+)"/g)].map((m) => m[1]),
);
if (declared.size === 0) {
  console.error(`No DemoItem ids found in ${registry}`);
  process.exit(1);
}

const walk = (dir) =>
  readdirSync(dir).flatMap((entry) => {
    const p = join(dir, entry);
    return statSync(p).isDirectory() ? walk(p) : p.endsWith('.mdx') || p.endsWith('.md') ? [p] : [];
  });

const problems = [];
for (const file of walk(join(root, 'src/content/docs'))) {
  const text = readFileSync(file, 'utf8');
  for (const m of text.matchAll(/<(?:Demo|IdiomDemos)\b[^>]*\bid=["']([^"']+)["']/g)) {
    if (!declared.has(m[1])) {
      problems.push(`${file.replace(root + '/', '')}: unknown demo id "${m[1]}"`);
    }
  }
}

// --- every component page appears in the hand-written sidebar ------------------------------

const config = readFileSync(join(root, 'astro.config.mjs'), 'utf8');
const listed = new Set(
  [...config.matchAll(/componentPages\(([^)]*)\)/g)].flatMap((m) =>
    [...m[1].matchAll(/'([a-z0-9-]+)'/g)].map((s) => s[1]),
  ),
);
const pages = readdirSync(join(root, 'src/content/docs/components'))
  .filter((f) => f.endsWith('.mdx') || f.endsWith('.md'))
  .map((f) => f.replace(/\.mdx?$/, ''));

for (const page of pages) {
  if (!listed.has(page)) problems.push(`components/${page}: not in the sidebar in astro.config.mjs`);
}
for (const slug of listed) {
  if (!pages.includes(slug)) problems.push(`astro.config.mjs: sidebar lists "components/${slug}", which has no page`);
}

if (problems.length) {
  console.error('Docs check failed:\n' + problems.map((p) => '  ' + p).join('\n'));
  console.error('\nRegistered demo ids: ' + [...declared].sort().join(', '));
  process.exit(1);
}
console.log(`Docs check passed (${declared.size} demo ids, ${pages.length} component pages).`);
