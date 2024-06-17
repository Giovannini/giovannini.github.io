import './dontmock';
import React from 'react';
import TestUtils from 'react-addons-test-utils';
import MyComponent from '../myComponent';

describe('MyComponent', () => {

  it("est composé d'un label", () => {
    const labelText = "Je suis le label";
    const textInput = TestUtils.renderIntoDocument(
      <MyComponent label={ labelText } />
    );
    const labels = TestUtils.scryRenderedDOMComponentsWithClass(textInput, "label");

    expect(labels.length).toBe(1);
    expect(labels[0].textContent).toBe(labelText);
  });

  it("est composé d'un input", () => {
    const value = "Je suis la valeur dans l'input";
    const textInput = TestUtils.renderIntoDocument(
      <MyComponent />
    );
    const inputs = TestUtils.scryRenderedDOMComponentsWithTag(textInput, "input");

    expect(inputs.length).toBe(1);
    expect(inputs[0].className).toBe("sexy-input");
  });

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

});
