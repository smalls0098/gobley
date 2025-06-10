import fs from "node:fs";
import path from "node:path";
import { themes as prismThemes } from "prism-react-renderer";
import type { Config } from "@docusaurus/types";
import type * as Preset from "@docusaurus/preset-classic";
import type { PluginOptions } from "docusaurus-plugin-llms-builder";
import packageJson from "./package.json";

// This runs in Node.js - Don't use client-side code here (browser APIs, JSX...)

const config: Config = {
  title: "Gobley",
  tagline: "Embed Rust into your Kotlin Multiplatform project",
  favicon: "img/favicon.ico",

  // Set the production url of your site here
  url: "https://gobley.dev",
  // Set the /<baseUrl>/ pathname under which your site is served
  // For GitHub pages deployment, it is often '/<projectName>/'
  baseUrl: "/",

  // GitHub pages deployment config.
  // If you aren't using GitHub pages, you don't need these.
  organizationName: "gobley", // Usually your GitHub org/user name.
  projectName: "gobley", // Usually your repo name.

  onBrokenLinks: "throw",
  onBrokenMarkdownLinks: "warn",

  // Even if you don't use internationalization, you can use this field to set
  // useful metadata like html lang. For example, if your site is Chinese, you
  // may want to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: "en",
    locales: ["en"],
  },

  plugins: [
    [
      "docusaurus-plugin-llms-builder",
      {
        version: packageJson.version,
        llmConfigs: [
          {
            title: "Gobley",
            description: "Embed Rust into your Kotlin Multiplatform project",
            summary: fs.readFileSync(path.join(__dirname, "..", "README.md"), {
              encoding: "utf-8",
            }),
            sessions: [
              {
                type: "docs",
                docsDir: "docs",
                sessionName: "Docs",
              },
            ],
            generateLLMsTxt: true,
            generateLLMsFullTxt: true,
          },
        ],
      } as PluginOptions,
    ],
  ],

  presets: [
    [
      "classic",
      {
        docs: {
          sidebarPath: "./sidebars.ts",
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          editUrl: "https://github.com/gobley/gobley/tree/main/docs",
        },
        theme: {
          customCss: "./src/css/custom.css",
        },
      } satisfies Preset.Options,
    ],
  ],

  themeConfig: {
    image: "img/Gobley-Social-Card.png",
    colorMode: {
      respectPrefersColorScheme: true,
    },
    navbar: {
      title: "Gobley",
      logo: {
        alt: "Gobley Logo",
        src: "img/logo.svg",
      },
      items: [
        {
          type: "docSidebar",
          sidebarId: "tutorialSidebar",
          position: "left",
          label: "Tutorial",
        },
        {
          type: "docSidebar",
          sidebarId: "documentationSidebar",
          position: "left",
          label: "Docs",
        },
        {
          type: "docsVersionDropdown",
          position: "right",
          dropdownActiveClassDisabled: true,
        },
        {
          type: "custom-githubStargazersCountButton",
          position: "right",
          repository: "gobley/gobley",
        },
      ],
    },
    footer: {
      style: "dark",
      links: [
        {
          title: "Tutorial",
          items: [
            {
              label: "Getting Started",
              to: "/docs/tutorial",
            },
          ],
        },
        {
          title: "Docs",
          items: [
            {
              label: "Overview",
              to: "/docs",
            },
          ],
        },
        {
          title: "More",
          items: [
            {
              label: "GitHub",
              href: "https://github.com/gobley/gobley",
            },
          ],
        },
      ],
      copyright: `Copyright © ${new Date().getFullYear()} Gobley Contributors.`,
    },
    prism: {
      theme: prismThemes.github,
      darkTheme: prismThemes.dracula,
    },
  } satisfies Preset.ThemeConfig,
};

export default config;
