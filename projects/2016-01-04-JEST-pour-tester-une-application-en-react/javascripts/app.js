import React from 'react';
import ReactDOM from 'react-dom';

import Input from './inputs/textInput';

let ApplicationBox = React.createClass({
  getInitialState() {
    return {
      value: "I'm an application Box"
    };
  },

  handleChange(event) {
    this.setState({ value: event.target.value });
  },

  render() {
    return (
      <div className="appBox">
          This is what is written in the input: '{ this.state.value }'.
          <Input label="Super input" value={ this.state.value } handleChange={ this.handleChange } />
      </div>
    );
  }
});
ReactDOM.render(
  <ApplicationBox />,
  document.getElementById('content')
);

