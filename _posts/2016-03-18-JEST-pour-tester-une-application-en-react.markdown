---
layout: post
title:  "Jest pour tester une application React"
date:   2016-03-18
categories: javascript, test, jest
comments: true
---

[Jest](https://facebook.github.io/jest/){:target="_blank"} est un framework permettant d'effectuer des tests unitaires en Javascript.
Il utilise un système d'assertion plutôt familier, se charge de mocker automatiquement les modules chargés et lance les différents tests exécutés en parallèle pour plus de vélocité.

Tout comme [React](https://facebook.github.io/react/){:target="_blank"}, Jest est développé par Facebook..
Il est souvent bon d'utiliser l'outil de test provenant de l'éditeur lorsqu'on utilise un librairie, c'est pourquoi je me penche aujourd'hui sur les tests via JEST pour une application de type React JS.

## Pourquoi mettre en place des tests ?

Mettre en place des tests automatiques, c'est pouvoir s'assurer du fonctionnement d'une application ainsi que de sa conformité avec le cahier des charges.
Le back-end est presque systématiquement soumis à des tests unitaires et beaucoup d'équipes ont une stratégie d'intégration continue qui les intègre à leurs stratégies d'automatisation.
Le front-end, par contre, est souvent délaissé, peu voir pas du tout testé.
Ainsi on retrouve souvent les équipes à faire des tests manuels, ouvrant leur navigateur et vérifiant que tout fonctionne correctement.

Cette méthode a plusieurs problèmes puisqu'elle ne permet pas du tout de vérifier correctement la majorité du code écrit, se concentrant uniquement sur le fonctionnel et le visuel. De plus, plus l'application évolue et grossis, plus il est complexe de pouvoir vérifier chaque ancienne fonctionnalité en plus des nouvelles. Ainsi, mettre en place des tests front-end structurés permet d'automatiser ce processus et de pouvoir développer l'interface graphique sans avoir à attendre d'avoir un backend fonctionnel. On gagne en productivité.

Enfin, les nouveaux frameworks et librairies front-end permettent de créer des modules réutilisables qu'il convient de valider et de tester afin de mesurer leur fiabilité, leur stabilité et leur ré-utilisabilité dans d'autres projets.

Les tests unitaires permettent de régler les problèmes liés à la confiance extrêmement faible que l'on peut avoir en notre code, la perte de temps considérable pour chaque vérification à l'ajout d'une nouvelle fonctionnalité et la maintenabilité ridicule du code.

## Pourquoi utiliser Jest ?

Jest est développé et maintenu par les ingénieurs de chez Facebook, tout comme l'est ReactJS.
Il est stable, performant et se rapproche grandement de [la philosophie de React](https://www.quora.com/What-is-the-philosophy-behind-React-js){:target="_blank"}.
Enfin, Jest est un framework de tests simple, standard et autonome puisqu'il est basé sur JsDom et construit à partir du framework [Jasmine](http://jasmine.github.io/){:target="_blank"}.

Les fonctionnalités phares de ce framework sont:

 * le mocking automatique des dépendances
 * le fait qu'il se charge de trouver les fichiers de tests automatiquement
 * le lancement des tests en parallèle selon une implémentation fake du DOM
 * la simplicité de configuration

Plutôt intéressant, non?


## Tester ses composants avec Jest

### La mise en place

Pour mettre en place Jest dans une application, il suffit de l'ajouter aux dépendances du projet.
A la racine du [projet associé à ce post](https://github.com/Giovannini/giovannini.github.io/tree/master/projects/2016-03-18-JEST-pour-tester-une-application-en-react){:target="_blank"}, tu trouveras le [package.json](https://github.com/Giovannini/giovannini.github.io/blob/master/projects/2016-03-18-JEST-pour-tester-une-application-en-react/package.json){:target="_blank"} que nous utiliserons pour ce projet ainsi qu'une configuration minimale pour démarrer un projet React.

Deux choses que l'on n'a pas l'habitude de voir sont intéressantes à noter quant au contenu de ce fichier.
Premièrement, cette ligne:

{% highlight javascript %}
"scriptPreprocessor": "<rootDir>/javascripts/tools/jestPreprocessor"
{% endhighlight %}

Elle indique que nous utiliserons un préprocesseur.
En effet, puisque nous allons écrire du code JSX (qui permet d'écrire avec React du HTML dans notre code javascript), nous allons avoir besoin de compiler ce code en simple Javascript avant l'exécution de nos tests avec Jest.
Le code pour ce préprocesseur peut être trouvé à divers endroit sur l'internet, mais voici celui que j'utilise pour ce projet: [./javascripts/tools/jestPreprocessor](https://github.com/Giovannini/giovannini.github.io/blob/master/projects/2016-03-18-JEST-pour-tester-une-application-en-react/javascripts/tools/jestPreprocessor.js){:target="_blank"}.

D'autre part, on aura remarqué les lignes:

{% highlight javascript %}
"unmockedModulePathPatterns": [
  "<rootDir>/node_modules/react",
  "<rootDir>/node_modules/react-dom",
  "<rootDir>/node_modules/react-addons-test-utils",
  "<rootDir>/node_modules/fbjs"
],
{% endhighlight %}

Elles permettent de signaler les modules que nous souhaitons, par défaut, ne pas mocker.
En effet, j'ai déjà dit que Jest se chargeait de mocker les modules que nous utilisons, et il le fait sérieusement.
Néanmoins, certains modules sont existentiels pour nous.
Si l'on veut pouvoir tester notre application correctement, certaines dépendances restent obligatoires.
Ici, on retrouve bêtement React ainsi qu'une collection d'outils pour travailler avec le DOM et réaliser des tests.

Ce [package.json](https://github.com/Giovannini/giovannini.github.io/blob/master/projects/2016-03-18-JEST-pour-tester-une-application-en-react/package.json){:target="_blank"} est un peu gros puisqu'il contient en plus du strict nécessaire pour notre projet, de quoi faire une compilation à la volée de notre code ES6 ainsi que de quoi lancer nos tests en boucle.

### Ecrire des tests

Après toute cette mise en place, nous pouvons enfin écrire nos tests. Nous verrons dans un second temps si nous pouvons faire un composant qui y réponde correctement.
Notre code sera écrit en ES6.
Découvrons donc ensemble à quoi ressemble un fichier de test avec Jest:

{% highlight javascript %}
jest.unmock('../textInput');
{% endhighlight %}

Pour écrire des tests unitaires efficaces, il est nécessaire d'isoler les unités de code que l'on souhaite tester pour ne tester que cela.
Jest se charge justement d'isoler un module de ses dépendances en générant automatiquement des mocks pour chacune des dépendances de ce module.
Ainsi, Jest va en effet mocker les fichiers qu'on lui fait importer, il est donc nécessaire de lui demander de ne pas mocker le module que l'on souhaite tester: c'est le but de cette ligne.

Evidemment, il est possible de configurer Jest pour qu'il ne mock pas les dépendances de manière automatique, on lui précisera alors les modules à mocker via `jest.mock(mon/module/a/mocker);`, mais autant ne pas utiliser Jest pour une utilisation pareille.

{% highlight javascript %}
import React from 'react';
import TestUtils from 'react-addons-test-utils';

const TextInput = require('../textInput');
{% endhighlight %}

Ici, on importe les modules dont on aura besoin lors du test. La seule particularité est que le module que l'on teste, `textInput`, est importé via la fonction require et non via un `import` ES6. Nous avions parlé du fait que Jest se charge d'isoler un module de ses dépendences en générant des mocks pour chacune des dépendances du module. Pour se faire, Jest implémente sa propre méthode `require()` se chargeant de charger le module réel, d'inspecter ce à quoi il ressemble et de mocker chacune des valeurs exportée. Ainsi, par défaut, il n'est pas possible d'utiliser les détails d'implémentation d'autres modules de façon accidentelle lorsque l'on teste de manière unitaire notre composant.

Ici, on utilise un import simple pour les dépendences `react` et `react-addons-test-utils` puisque désactivons, au niveau des fichiers de configuration, les mocks automatiques de ces modules dont les méthodes sont justement utilisées pour tester nos composants. Néanmoins, nous avons besoin d'utiliser la méthode `require()` pour notre module `textInput` puisque nous ne précisons qu'il ne doit pas être mocké qu'en haut du fichier et que les `imports` sont prioritaires sur les autres méthodes de notre fichier de test. Le module que nous testons serait alors mocké si nous utilisions un import comme pour les autres et il s'agit d'une chose que nous voulons à tout prix éviter. L'utilisation de la méthode require est nécessaire pour empêcher ce mock automatique.

<div class="tips">
Si l'utilisation d'un import est vital pour conserver un code d'une grâce incommensurable, il est possible d'utiliser une petite astuce consistant à écrire la ligne de mock dans un autre fichier et de l'importer. Ainsi, puisque les imports ES6 s'exécutent dans l'ordre, on a la certitude que l'import de notre module peu se faire après l'exécution de la méthode qui s'appelle <code>jest.unmock()</code>.
</div>

Notons que les deux imports précédents devraient être par défaut uniquement des mocks puisqu'on les importe en utilisant le `import` de ES6 mais qu'ayant précisé dans le `package.json` que nous ne souhaitions pas les mocker, cela ne se fera donc pas.

{% highlight javascript %}
describe('TextInput', () => {
  it("est composé d'un label", () => {
    const labelText = "Je suis le label";
    const textInput = TestUtils.renderIntoDocument(
      <TextInput label={ labelText } />
    );
    const labels = TestUtils.scryRenderedDOMComponentsWithClass(textInput, "label");

    expect(labels.length).toBe(1);
    expect(labels[0].textContent).toBe(labelText);
  });
});
{% endhighlight %}

Enfin, nous exécutons un test. Dans un premier temps, nous créons notre composant puis nous utilisons les fonctions utilitaires de Jest pour le charger dans le DOM. On retrouve ici l'implémentation fake du DOM dont nous avons parlé précédemment avec la méthode `renderIntoDocument` des `TestUtils`. Ensuite nous utilisons une méthode avec un nom à rallonge, [`scryRenderedDOMComponentsWithClass()`](https://facebook.github.io/react/docs/test-utils.html#scryrendereddomcomponentswithclass){:target="_blank"} qui nous permet de trouver dans le composant que l'on passe en entrée, tous les composants ayant une classe particulière.
Enfin nous trouvons nos assertions: nous souhaitons que ce soit le seul composant avec la classe label, donc le seul élément dans le tableau que retourne `scryRenderedDOMComponentsWithClass`, et aussi que le texte dans ce composant soit bien celui que l'on a passé en props.

La comparaison via `.toBe()` est en fait une comparaison utilisant `===`. [De nombreux autres éléments de comparaison existent](https://facebook.github.io/jest/docs/api.html#expect-value){:target="_blank"} permettant des comparaisons diverses. On peut utiliser `.toEqual()` pour une comparaison profonde (deep comparison) entre deux objets.

Le code que nous avons dû écrire pour ce test est très concis et tiens en trois étapes:

 1. On dessine le composant
 2. On récupère son noeud dans le DOM
 3. On vérifie les valeurs attendues

Généralement, on n'a pas besoin de plus, si ce n'est peut être une étape supplémentaire qui s'occupe de générer un événement dans notre composant.

## Exécuter des tests

Ecrivons alors un composant React qui répond positivement à ce test:

{% highlight javascript %}
import React from 'react';

export default React.createClass({
  propTypes: {
    label: React.PropTypes.string
  },

  render() {
    return (
      <div className="form-elm">
        <p className="label">{ this.props.label }</p>
      </div>
    );
  }
});
{% endhighlight %}

Pas trop de folies ici, nous avons fait un test simple, voilà un composant React simplissime.

Puisque nous nous sommes occupés de toute la configuration au début, nous devrions être en état de lancer notre test très simplement via la commande `npm test`.

{% highlight javascript %}
Using Jest CLI v0.9.0, jasmine2
 PASS  javascripts/inputs/__tests__/textInput-test.js (0.645s)
1 test passed (1 total in 1 test suites, run time 1.746s)
{% endhighlight %}

Et tout s'est bien passé, notre test s'est bien passé, notre composant est correctement et rapidement testé.

## L'utilisation des mocks

Abordons pour terminer l'utilisation des mocks avec Jest.
Rajoutons pour ce faire un test à notre fichier.
Nous considérons à présent que notre composant possède une checkbox.
Chaque clic sur cet input devra déclencher un appel à une méthode `onClick` passée en props au composant.

{% highlight javascript %}
it('appelle la méthode handleChangeMethod passé en props à chaque modification', () => {

  // Génération d'une fonction mock
  const onClick = jest.genMockFunction();

  const textInput = TestUtils.renderIntoDocument(
    <MyComponent label="test" value="This is a test." onClick={ onClick } />
  );

  expect(onClick.mock.calls.length).toBe(0);

  // Simulation d'une modification
  TestUtils.Simulate.click(
    TestUtils.findRenderedDOMComponentWithTag(textInput, 'input')
  );
  expect(onClick.mock.calls.length).toBe(1);
});
{% endhighlight %}

Jest présente de nombreuses méthodes dont `.genMockFunction()` qui permet, comme son nom peut laisser entendre, de générer des mocks de fonctions facilement.
Il s'agit exactement des mocks qui sont générés automatiquement lors de l'import de modules.
On voit ici qu'elles permettent par exemple de compter le nombre d'appel à ce mock, mais ce n'est bien sur qu'[un début](https://facebook.github.io/jest/docs/mock-functions.html#content){:target="_blank"}.

## Conclusion

Je n'irais pas plus loin dans ce post, la base a été présentée.
Jest est un outil de tests performant et qui révèle toute son utilité lorsqu'il est utilisé pour tester des composants avec de nombreuses dépendances.
Il permet, avec peu de configuration, une simplicité d'utilisation décuplée.
On peut tout de même remarquer que les tests peuvent prendre un peu plus longtemps que sur des frameworks comme [Mocha](https://mochajs.org/){:target="_blank"}.
Néanmoins son utilisation pour les tests unitaires de composants React est réellement intéressante et apporte de très nombreux bénéfices comme nous avons pu le voir et compensent facilement ce défaut.

* [La documentation de Jest](https://facebook.github.io/jest/docs/api.html#content)
* [Les sources pour ce projet](https://github.com/Giovannini/giovannini.github.io/tree/master/projects/projects/2016-03-18-JEST-pour-tester-une-application-en-react/)
