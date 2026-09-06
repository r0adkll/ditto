// @ts-check
import { defineConfig } from 'astro/config';
import starlight from '@astrojs/starlight';

export default defineConfig({
  site: 'https://r0adkll.github.io',
  base: '/ditto',
  integrations: [
    starlight({
      title: 'Ditto',
      description: 'A Compose Multiplatform UI toolkit with one component API and three platform-adaptive idioms.',
      logo: { src: './src/assets/mascot.svg', alt: 'Ditto' },
      favicon: '/mascot.svg',
      customCss: ['./src/styles/brand.css'],
      social: [{ icon: 'github', label: 'GitHub', href: 'https://github.com/r0adkll/ditto' }],
      sidebar: [
        { label: 'Start here', items: [{ label: 'Overview', slug: '' }] },
        { label: 'Foundations', items: [{ autogenerate: { directory: 'foundations' } }] },
        { label: 'Components', items: [{ autogenerate: { directory: 'components' } }] },
        { label: 'Reference', items: [{ label: 'API (Dokka)', link: '/api/', attrs: { target: '_blank' } }, { label: 'Full catalog', link: '/catalog/', attrs: { target: '_blank' } }] },
      ],
    }),
  ],
});
