import React from 'react';

const SearchFilterBar = ({
  placeholder = 'Search...',
  onSearch,
  filters = [],
  onFilter,
}) => {
  return (
    <div className="search-filter-bar">
      <input
        type="text"
        className="search-input"
        placeholder={placeholder}
        onChange={(e) => onSearch && onSearch(e.target.value)}
      />
      {filters && filters.length > 0 && (
        <select
          className="filter-select"
          onChange={(e) => onFilter && onFilter(e.target.value)}
        >
          {filters.map((filter) => (
            <option key={filter.value} value={filter.value}>
              {filter.label}
            </option>
          ))}
        </select>
      )}
    </div>
  );
};

export default SearchFilterBar;