import React from 'react';

const EmptyState = ({ title, message, actionText, onAction }) => {
  return (
    <div className="empty-state">
      <div className="empty-state-icon">📋</div>
      <h3>{title}</h3>
      <p>{message}</p>
      {actionText && onAction && (
        <button type="button" className="primary-btn" onClick={onAction}>
          {actionText}
        </button>
      )}
    </div>
  );
};

export default EmptyState;