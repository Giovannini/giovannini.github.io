---
layout: post
title: Mise en place de less
date: 2015-12-27T03:00:08.000Z
categories: 'CSS, Front'
comments: true
---

Less est une extension au langage CSS qui permet d'y ajouter des fonctionnalités permettant d'écrire du CSS plus maintenable, par exemple via l'utilisation de variables, de mixins ou encore de fonctions.

Il existe des librairies tiers qui permettent de compiler du code Less en code CSS pour qu'il soit utilisable dans tous les navigateurs. Ma curiosité (couplée à l'envie de faire quelque chose de pas trop pourri pour ce site) m'a pousée à essayer de mettre en place un environnement qui me permettrait d'écrire du code un peu compréhensible et que je pourrais faire évoluer ensuite.

Le but de cet article est de décrire les outils ainsi que l'architecture que j'ai mis en place pour écrire du CSS sereinement.

## Installation et utilisation
Tout d'abord, j'ai installé le compilateur [less](http://lesscss.org/#using-less) qui permet de transcrire du code Less en CSS tout simple. J'ai voulu faire les choses propres, j'ai écrit un petit package.json pour le montrer:

{% gist Giovannini/17a05e8f53546e1e5f23 %}

Du coup après ça je run un joli petit `npm install` et je peux utiliser le compilateur. D'utilisation c'est tout bête, il suffit de lancer la commande `lessc` suivie du fichier less à compiler. Si on ne précise rien, ça va juste l'écrire dans la console, mais on peut rediriger la sortie standard vers le fichier de notre choix en ajoutant un second nom de fichier à la suite de notre commande.

On se retrouve avec un joli `lessc style.less style.css` comme ils font dans le Getting Started dans le site de less.

## Une architecture pour organiser un peu le tout
 Un des avantages de less est qu'il aide à améliorer l'organisation des feuilles de style et même le style qu'on écrit à l'intérieur de ces feuilles. Une bonne organisation via une architecture de fichiers propre, ça aide à s'y retrouver dans tout           ce qu'on peut écrire et donc clairement nous faciliter la vie dans le futur. C'est donc ce qu'on vise. Des architectures il y en a plein de possible. Chacune a ses pour et ses contres, moi je vais utiliser celle qui suit:
- base/ : contient les styles globaux comme les couleurs, les polices, etc.
- components/ : contient chaque composant séparément, dans son propre fichier .less
- layout/ : contient les styles des composants layout comme les header, footers ou encore les nav.
- pages/ : contient des styles spécifiques à certaines pages.
- themes/ : contientdes styles propres aux différents thèmes.
- utils/ : contient les mixins globales, les fonctions, etc.
- vendors/ : contient les styles ou mixins écrits par d'autres
- main.less : fichier de output qui rassemble tout ce qu'il y a dans les dossiers sus-cités.

Le fonctionnement global c'est que dans chacun des dossiers, on va avoir un fichier .less qui va se charger de rassembler tout ce qu'il y a dans son dossier. On va l'appeler `_module.less`, mais dès que j'aurais le dos tourné, tu feras ce que tu voudras du nom. Ces modules, on va ainsi pouvoir les importer facilement dans notre fichier `main.less` grâce à la fonctionnalité d'import que nous propose Less. Voici donc globalement à quoi va ressembler un fichier `main.less`:

{% gist Giovannini/6facddddb2f7d9657a61 %}

Ce qui est cool ici, c'est que tu n'auras à compiler qu'un unique fichier less, le `main.less`, et qu'il contiendra tout ce dont tu as besoin pour ta page. Tu n'auras donc aussi que ce module à importer dans ton HTML. Il existe de nombreux articles expliquant la façon d'assembler ses composants et les organiser clairement avec ce genre d'architecture, mais cela est une autre histoire.

## Automatiser le processus
Après ce travail, nous disposons d'une façon d'écrire et d'organiser du code plutôt joli pour du css. Un défaut à cette méthode demeurre tout de même. En effet, pour chaque changement fait dans nos fichiers .less, il est nécessaire de relancer           la compilation. Au début, c'est rigolo, mais au bout d'un certain moment, on a juste envie que les modifications que l'on fait prennent effet de suite, à la limite en rechargeant la page juste. Les outils qui permettent de faire ce genre de           prouesse s'appellent des           _watchers_. Concrètement, il s'agit de petits scripts qui observent une arborescence de fichiers pour se rendre compte d'un changement lorsqu'il survient; une commande est alors lancée automatiquement. Dans notre cas, nous allons vouloir           observer tous nos fichiers .less et lancer la commande de compilation de notre fichier main.less lorsqu'un changement survient. Pour ce faire, nous allons ajouter une dépendence à notre projet, [watch](https://www.npmjs.com/package/watch).           Il en existe plusieurs autres, bien sûr, et certainement avec plus de fonctionnalités badass. Ici, le point est que ce genre d'outil existe, et que c'est cool pour les fainéants que nous sommes. Pour simplifier l'utilisation de ce nouveau module,           nous allons écrire quelques scripts npm. En effet, nous avons ici un outil que nous lancerons à chaque fois que nous souhaiterons développer le style de notre application. Si pour le lancer, une commande à rallonge est nécessaire, nous aurons           besoin de notre mémoire pour nous en rappeler. Celle-ci étant bien trop précieuse pour être gaspillée sur de telles histoires, nous allons faire un petit effort supplémentaire aujourd'hui et oublier le tout à jamais.

{% gist Giovannini/31bd966d703ba1dd6add %}

Reprenons ces lignes tranquillement:
- `build-less`: est une copie pure et simple de ce que nous utilisions en ligne de commande précédemment.
- `watch-less`: permet d'utiliser le nouvel outil que nous venons d'installer. Il s'utilise de manière très simple: on appelle la commande           `watch`, on précise la commande que l'on veut lancer lorsqu'un changement a lieu, puis le dossier à partir duquel on veut que les changements soient observés.
- `build`: est un raccourci pour le script           _build-less_ écrit précédemment.
- `watch`: est un raccourci pour le script           _watch-less_ écrit précédemment.
- `start`: est un raccourci qui permet de lancer le watch sur nos fichiers less

Grâce à cette logique, nous allons pouvoir démarrer notre watch en lançant npm start et ainsi commencer à écrire nos fichiers less et voir via un simple rechargement           de page les changements efectués. J'ai ajouté raccourcis de scripts qui semblent inutiles ici, qui le sont en effet pour le moment, mais qui pourront s'avérer utile lorsque vous aurez besoin d'utiliser un watcher sur vos fichiers js par exemple           (par exemple dans le but de les transpiler automatiquement de ES6 vers ES5). Il sera allors simple de modifier le script actuel watch en quelque chose comme           `npm run watch-less & npm run watch-js`.

## Pour conclure
Nous avons effectué un travail qui nous permet d'utiliser du less comme nous utilisons du CSS à l'utilisation, puisqu'il suffit d'un rechargement de page pour que le changement ait lieu, en se passant de répéter l'étape oh combien barbante de           recompiler via la console notre code less. Il pourrait être intéressant d'aller encore plus loin et de faire recharger notre page toute seule lorsque nous effectuons un changement dans notre code, pour ainsi économiser cet appui de touche si           coûteux qu'est le F5. Des outils existent pour cela, mais je trouve que tu pousses ta flemme un peu trop loin et je n'en parlerais donc pas ici.
