import React from 'react';

const StatCards = ({ stats }) => {
  if (!stats) return null;

  const cards = [
    {
      title: 'Total Volunteers',
      value: stats.totalVolunteers ?? 0,
      color: '#2563eb',
    },
    {
      title: 'Active Organizations',
      value: stats.approvedOrganizations ?? 0,
      color: '#059669',
    },
    {
      title: 'Open Opportunities',
      value: stats.openOpportunities ?? 0,
      color: '#d97706',
    },
    {
      title: 'Impact Reports',
      value: stats.totalImpactReports ?? 0,
      color: '#7c3aed',
    },
  ];

  return (
    <div className="stat-cards-row">
      {cards.map((card, idx) => (
        <div
          key={idx}
          className="stat-card"
          style={{ borderLeft: `4px solid ${card.color}` }}
        >
          <div className="stat-card-title">{card.title}</div>
          <div className="stat-card-value">{card.value}</div>
        </div>
      ))}
    </div>
  );
};

export default StatCards;