jest.dontMock('../textInput');

import React from 'react';
import ReactDOM from 'react-dom';
import TestUtils from 'react-addons-test-utils';

const TextInput = require('../textInput');

describe('TextInput', () => {

  it('call handleChangeMethod from props on change', () => {

    // Render a checkbox with label in the document
    let handleChange = jest.genMockFunction();

    var textInput = TestUtils.renderIntoDocument(
      <TextInput label="test" value="This is a test." handleChange={ handleChange } />
    );

    // Verify that text is "This is a test." by default
    expect(handleChange.mock.calls.length).toBe(0);

    // Simulate a onChange event and verify that the handleChange method has been called.
    TestUtils.Simulate.change(
      TestUtils.findRenderedDOMComponentWithTag(textInput, 'input'),
      { target: { value: "This is a new value." } }
    );
    expect(handleChange.mock.calls.length).toBe(1);
  });

});