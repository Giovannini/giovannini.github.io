import React from 'react';

export default React.createClass({
  propTypes: {
    label: React.PropTypes.string,
    value: React.PropTypes.string,
    onClick: React.PropTypes.func
  },

  getDefaultProps() {
    return {
      onClick: () => {}
    };
  },

  render() {
    const { label, ...props } = this.props;

    return (
      <div className="form-elm">
        <p className="label">
          { label }
        </p>

        <p className="data">
          <input
            type="checkbox"
            className="sexy-input"
            { ...props }
          />
        </p>
      </div>
    );
  }
});
