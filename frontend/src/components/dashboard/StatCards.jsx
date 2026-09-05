import React, { useState } from 'react';

const StatCards = ({ stats, onSelectCard, selectedCard }) => {
  const [hoveredIdx, setHoveredIdx] = useState(null);

  if (!stats) return null;

  const cards = [
    {
      id: 'volunteers',
      title: 'Total Volunteers',
      value: stats.totalVolunteers ?? 0,
      color: '#2563eb',
      icon: '👥',
      subtext: 'Registered changemakers',
      badge: 'Community',
    },
    {
      id: 'organizations',
      title: 'Active Organizations',
      value: stats.approvedOrganizations ?? 0,
      color: '#059669',
      icon: '🏛️',
      subtext: 'Verified non-profits',
      badge: `${stats.pendingOrganizations ?? 0} Pending`,
    },
    {
      id: 'opportunities',
      title: 'Open Opportunities',
      value: stats.openOpportunities ?? 0,
      color: '#d97706',
      icon: '🎯',
      subtext: 'Ready for enrollments',
      badge: 'Active Hub',
    },
    {
      id: 'reports',
      title: 'Impact Reports',
      value: stats.totalImpactReports ?? 0,
      color: '#7c3aed',
      icon: '✨',
      subtext: 'Stories & hours logged',
      badge: 'Logged Impact',
    },
  ];

  return (
    <div className="stat-cards-row">
      {cards.map((card, idx) => {
        const isSelected = selectedCard === card.id;
        const isHovered = hoveredIdx === idx;

        return (
          <div
            key={card.id}
            className={`stat-card interactive-card ${isSelected ? 'active-metric' : ''}`}
            style={{
              borderLeft: `4px solid ${card.color}`,
              transform: isHovered || isSelected ? 'translateY(-4px)' : 'none',
            }}
            onMouseEnter={() => setHoveredIdx(idx)}
            onMouseLeave={() => setHoveredIdx(null)}
            onClick={() => onSelectCard && onSelectCard(card.id)}
            role="button"
            tabIndex={0}
          >
            <div className="stat-card-header">
              <span className="stat-card-title">{card.title}</span>
              <span className="stat-card-icon">{card.icon}</span>
            </div>

            <div className="stat-card-value-wrap">
              <div className="stat-card-value">{card.value}</div>
              <span
                className="stat-mini-badge"
                style={{
                  backgroundColor: `${card.color}15`,
                  color: card.color,
                  borderColor: `${card.color}30`,
                }}
              >
                {card.badge}
              </span>
            </div>

            <div className="stat-card-footer">
              <span className="stat-card-subtext">{card.subtext}</span>
              <span className="interactive-cue">
                {isSelected ? '● Focused' : 'Inspect →'}
              </span>
            </div>
          </div>
        );
      })}
    </div>
  );
};

export default StatCards;