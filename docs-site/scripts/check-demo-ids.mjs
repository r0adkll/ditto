#!/usr/bin/env node
/**
 * Demo ids are public API: docs pages embed `/catalog/?id=<id>`, so a rename silently breaks a
 * published page. Fail the build when a page references an id the Kotlin registry doesn't define.
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

if (problems.length) {
  console.error('Demo id check failed:\n' + problems.map((p) => '  ' + p).join('\n'));
  console.error('\nRegistered ids: ' + [...declared].sort().join(', '));
  process.exit(1);
}
console.log(`Demo id check passed (${declared.size} registered ids).`);
