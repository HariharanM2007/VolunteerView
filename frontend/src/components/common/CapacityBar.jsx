import React from 'react';

const CapacityBar = ({ current = 0, max = 0 }) => {
  const percentage = max > 0 ? Math.min((current / max) * 100, 100) : 0;
  
  let barColor = '#2563eb';
  if (percentage >= 100) {
    barColor = '#dc2626';
  } else if (percentage >= 80) {
    barColor = '#d97706';
  }

  return (
    <div className="capacity-bar-container">
      <div className="capacity-bar-labels">
        <span>Capacity</span>
        <span>{current} / {max}</span>
      </div>
      <div className="capacity-bar-track">
        <div
          className="capacity-bar-fill"
          style={{ width: `${percentage}%`, backgroundColor: barColor }}
        />
      </div>
    </div>
  );
};

export default CapacityBar;