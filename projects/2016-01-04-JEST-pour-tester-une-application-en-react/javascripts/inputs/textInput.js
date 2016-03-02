import React from 'react';

export default React.createClass({
  propTypes: {
    label: React.PropTypes.string,
    value: React.PropTypes.string,
    handleChange: React.PropTypes.func
  },

  render() {
    const { label, value, handleChange } = this.props;

    return (
      <div className="form-elm">
        <p className="label">
          { label }
        </p>

        <p className="data">
          <input
            className="sexy"
            value={ value }
            onChange={ handleChange }
          />
        </p>
      </div>
    );
  }
});