/**
 * Creating a sidebar enables you to:
 - create an ordered group of docs
 - render a sidebar for each doc of that group
 - provide next/previous navigation

 The sidebars can be generated from the filesystem, or explicitly defined here.

 Create as many sidebars as you want.
 */

// @ts-check

/** @type {import('@docusaurus/plugin-content-docs').SidebarsConfig} */
const sidebars = {
  // By default, Docusaurus generates a sidebar from the docs folder structure
  // tutorialSidebar: [{type: 'autogenerated', dirName: '.'}],
  docs: [
    'gql',
    {
      collapsed: false,
      type: 'category',
      label: "Overview",
      items: [
        "overview/full_example",
        "overview/modules"
      ]
    },
    {
      collapsed: false,
      type: 'category',
      label: "Server",
      items: [
        {
          collapsed: false,
          type: 'category',
          label: "Schema",
          items: [
            "server/schema/output_types",
            "server/schema/input_types",
            "server/schema/dsl",
            "server/schema/resolvers",
            "server/schema/schema",
            "server/schema/context",
            "server/schema/error_handling",
            "server/schema/compiler",
          ]
        },
        {
          collapsed: true,
          type: 'category',
          label: "Execution",
          items: [
            "server/execution/planning",
            "server/execution/statistics",
          ]
        },
        {
          collapsed: false,
          type: 'category',
          label: "Integrations",
          items: [
            "server/integrations/http4s",
            "server/integrations/graphqlws",
            "server/integrations/natchez",
            "server/integrations/goi",
          ]
        }
      ]
    },
    {
      collapsed: false,
      type: 'category',
      label: "Client",
      items: [
        "client/dsl",
        "client/code-generation",
        {
          collapsed: false,
          type: 'category',
          label: "Integrations",
          items: [
            "client/integrations/http4s"
          ]
        },
        
      ]
    }
  ]

  // But you can create a sidebar manually
  // mdoc: [
  //   {
  //     type: "autogenerated",
  //     dirName: "../modules/docs/target/mdoc"
  //     // items: ["test"],
  //   }
  // ]
  // 'intro',
  // 'hello',
  // {
  //   type: 'category',
  //   label: 'Tutorial',
  //   items: ['tutorial-basics/create-a-document'],
  // },
  // ],
};

module.exports = sidebars;
