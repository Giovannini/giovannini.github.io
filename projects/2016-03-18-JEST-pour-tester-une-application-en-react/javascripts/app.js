import React from 'react';
import ReactDOM from 'react-dom';

import Input from './inputs/textInput';

let ApplicationBox = React.createClass({
  render() {
    return (
      <Input />
    );
  }
});

ReactDOM.render(
  <ApplicationBox />,
  document.getElementById('content')
);
