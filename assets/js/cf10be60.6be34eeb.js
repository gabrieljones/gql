"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[864],{3905:(e,t,n)=>{n.d(t,{Zo:()=>p,kt:()=>d});var r=n(7294);function a(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function o(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){a(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function s(e,t){if(null==e)return{};var n,r,a=function(e,t){if(null==e)return{};var n,r,a={},i=Object.keys(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}var l=r.createContext({}),c=function(e){var t=r.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):o(o({},t),e)),n},p=function(e){var t=c(e.components);return r.createElement(l.Provider,{value:t},e.children)},u={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},h=r.forwardRef((function(e,t){var n=e.components,a=e.mdxType,i=e.originalType,l=e.parentName,p=s(e,["components","mdxType","originalType","parentName"]),h=c(n),d=a,f=h["".concat(l,".").concat(d)]||h[d]||u[d]||i;return n?r.createElement(f,o(o({ref:t},p),{},{components:n})):r.createElement(f,o({ref:t},p))}));function d(e,t){var n=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var i=n.length,o=new Array(i);o[0]=h;var s={};for(var l in t)hasOwnProperty.call(t,l)&&(s[l]=t[l]);s.originalType=e,s.mdxType="string"==typeof e?e:a,o[1]=s;for(var c=2;c<i;c++)o[c]=n[c];return r.createElement.apply(null,o)}return r.createElement.apply(null,n)}h.displayName="MDXCreateElement"},9402:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>l,contentTitle:()=>o,default:()=>u,frontMatter:()=>i,metadata:()=>s,toc:()=>c});var r=n(7462),a=(n(7294),n(3905));const i={title:"Digraph philosophy"},o=void 0,s={unversionedId:"schema/graph_philosophy",id:"schema/graph_philosophy",title:"Digraph philosophy",description:"When designing data structures that allow recursion some tradeoffs must be made.",source:"@site/docs/schema/graph_philosophy.md",sourceDirName:"schema",slug:"/schema/graph_philosophy",permalink:"/gql/docs/schema/graph_philosophy",draft:!1,editUrl:"https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/docs/schema/graph_philosophy.md",tags:[],version:"current",frontMatter:{title:"Digraph philosophy"},sidebar:"docs",previous:{title:"Error handling",permalink:"/gql/docs/schema/error_handling"},next:{title:"Planning",permalink:"/gql/docs/execution/planning"}},l={},c=[{value:"Refernce by id",id:"refernce-by-id",level:2},{value:"Refernce by value",id:"refernce-by-value",level:2}],p={toc:c};function u(e){let{components:t,...n}=e;return(0,a.kt)("wrapper",(0,r.Z)({},p,n,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("p",null,"When designing data structures that allow recursion some tradeoffs must be made."),(0,a.kt)("p",null,"One such choice is, how do nodes reference other nodes?\nOne of two choices can be made that have different implications."),(0,a.kt)("h2",{id:"refernce-by-id"},"Refernce by id"),(0,a.kt)("p",null,"In the reference by id approach, each node has a unique id, say, it's GraphQL name.\nNodes reference other nodes by their id, with no regard for the implementation of the referenced nodes.\nThis approach is simple and easy to implement, but it has a few drawbacks."),(0,a.kt)("ul",null,(0,a.kt)("li",{parentName:"ul"},"There is no compile-time guarentee that every referenced node has a corresponding definition (at least not a simple one)."),(0,a.kt)("li",{parentName:"ul"},"Assuming structural information of a node is also forbidden, since the node implementation is not tied to it's id.\n(for instance, like using an interface's fields in a subtype's implementation)"),(0,a.kt)("li",{parentName:"ul"},"Since the id is the only information a reference has, automatic discovery of the schema is impossible, which burdens the end-user with the task of explicitly supplying implementations for every node.")),(0,a.kt)("h2",{id:"refernce-by-value"},"Refernce by value"),(0,a.kt)("p",null,"The reference by value choice is the one this library takes.\nIt imposes some differences that disallow ambiguity unless the user explicitly opts in to it."),(0,a.kt)("admonition",{type:"note"},(0,a.kt)("p",{parentName:"admonition"},"Implementation wise, converting one model to the other is not difficult.\nIf your need for this arises, please open an issue.")),(0,a.kt)("p",null,"In the reference by value approach, each node references other nodes by their value; their implementation.\nThis approach has a few advantages over the reference by id approach."),(0,a.kt)("ul",null,(0,a.kt)("li",{parentName:"ul"},"There is a compile-time guarentee that every referenced node has a corresponding definition."),(0,a.kt)("li",{parentName:"ul"},"Since the implementation is tied to the node, structural information can be extracted and used."),(0,a.kt)("li",{parentName:"ul"},"The user should only supply as little information as necessary.")),(0,a.kt)("p",null,'Unfortunately, this approach has a few drawbacks.\nThe most noticeble drawback is that during construction of (mutually) recursive types, either the user must "tie the loop",\nor opt out of safety.\nDefining types with functions (',(0,a.kt)("inlineCode",{parentName:"p"},"def"),") instead of ",(0,a.kt)("inlineCode",{parentName:"p"},"lazy val")," can cause a situation where the structure cannot be validated,\nsince every time the type is referenced, it is a new instance.\nFortunately there are many ways to work around this limitation."))}u.isMDXComponent=!0}}]);