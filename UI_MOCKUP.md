# Referral Network UI Mockup

Due to disk space constraints, this document provides a detailed mockup of the UI design for the Referral Network application.

## Architecture Overview

The UI application would be built using:
- **React** for component-based UI
- **Redux** for state management
- **Tailwind CSS** for styling
- **D3.js** for network visualization
- **React Router** for navigation

## UI Components

### 1. Navigation Bar
```
+--------------------------------------------------------------+
| Referral Network Dashboard    | Add Referral | View Analytics |
+--------------------------------------------------------------+
```

### 2. Network Visualization (Home Page)
```
+--------------------------------------------------------------+
|                                                              |
|                     [Interactive Graph]                      |
|                                                              |
|                                                              |
|                                                              |
|                                                              |
+--------------------------------------------------------------+
| Legend:                                                      |
| ● User Node  → Referral Relationship                         |
+--------------------------------------------------------------+
```

### 3. Add Referral Form
```
+--------------------------------------------------------------+
| Add New Referral                                             |
+--------------------------------------------------------------+
| Referrer:  [Dropdown of existing users]                      |
| Candidate: [Text Input]                                      |
|                                                              |
| [Add Referral]                                               |
+--------------------------------------------------------------+
| Status: Success/Error messages appear here                   |
+--------------------------------------------------------------+
```

### 4. Analytics Dashboard
```
+--------------------------------------------------------------+
| Referral Network Analytics                                   |
+--------------------------------------------------------------+
| Top Referrers by Reach                     | Unique Reach    |
| +---------------------------+               | Expansion       |
| | 1. User A (15 referrals)  |               | +-------------+|
| | 2. User B (12 referrals)  |               | | 1. User C   ||
| | 3. User C (8 referrals)   |               | | 2. User A   ||
| +---------------------------+               | | 3. User D   ||
|                                             | +-------------+|
+--------------------------------------------------------------+
| Flow Centrality (Network Brokers)          | Network Stats   |
| +---------------------------+               | +-------------+|
| | 1. User D (score: 25)     |               | | Total Users:  |
| | 2. User E (score: 18)     |               | | 42           |
| | 3. User F (score: 12)     |               | |              |
| +---------------------------+               | | Total Links:  |
|                                             | | 65           |
+--------------------------------------------------------------+
```

### 5. Network Growth Simulation
```
+--------------------------------------------------------------+
| Network Growth Simulation                                    |
+--------------------------------------------------------------+
| Parameters:                                                  |
| Probability (p): [0.1]  Days to Simulate: [30]               |
|                                                              |
| [Run Simulation]                                             |
+--------------------------------------------------------------+
|                                                              |
|                     [Growth Chart]                           |
|                                                              |
|                                                              |
+--------------------------------------------------------------+
| Days to reach 1000 referrals: 42                             |
+--------------------------------------------------------------+
```

### 6. Referral Bonus Optimization
```
+--------------------------------------------------------------+
| Referral Bonus Optimization                                  |
+--------------------------------------------------------------+
| Parameters:                                                  |
| Bonus Amount: [$100]  Target Hires: [50]                     |
|                                                              |
| [Calculate Optimal Bonus]                                    |
+--------------------------------------------------------------+
|                                                              |
|                 [Cost-Benefit Chart]                         |
|                                                              |
|                                                              |
+--------------------------------------------------------------+
| Minimum bonus required: $75                                  |
| Estimated total cost: $3,750                                 |
+--------------------------------------------------------------+
```

## Implementation Details

### Redux Store Structure
```javascript
{
  referralNetwork: {
    users: {}, // Map of user IDs to their direct referrals
    loading: false,
    error: null
  },
  simulation: {
    results: [],
    daysToTarget: null,
    loading: false
  }
}
```

### Key Components

1. **NetworkGraph**: D3.js force-directed graph visualization of the referral network
2. **ReferralForm**: Form for adding new referrals with validation
3. **TopReferrersTable**: Displays ranked list of referrers by total reach
4. **UniqueReachTable**: Shows users with maximum unique coverage
5. **FlowCentralityTable**: Displays critical network brokers
6. **SimulationControls**: Interface for running network growth simulations
7. **GrowthChart**: Line chart showing network growth over time

### API Integration

The UI would connect to a backend API that implements the Java-based referral network functionality. API endpoints would include:

- `POST /api/referrals` - Add a new referral
- `GET /api/referrals/:userId` - Get direct referrals for a user
- `GET /api/analytics/total-count/:userId` - Get total referral count
- `GET /api/analytics/top-referrers/:k` - Get top k referrers
- `GET /api/analytics/unique-reach/:k` - Get unique reach expansion
- `GET /api/analytics/flow-centrality/:k` - Get flow centrality
- `POST /api/simulation/growth` - Run network growth simulation
- `GET /api/simulation/days-to-target/:target` - Calculate days to target

## User Experience Flow

1. User views the network visualization on the home page
2. User can add new referrals through the Add Referral form
3. User can view analytics to identify influential users
4. User can run simulations to predict network growth

## Responsive Design

The UI would be fully responsive, adapting to different screen sizes:
- Desktop: Full visualization with side-by-side analytics
- Tablet: Stacked layout with scrollable sections
- Mobile: Simplified visualization with collapsible panels

## Accessibility Features

- High contrast mode for network visualization
- Keyboard navigation for all interactive elements
- Screen reader compatible tables and charts
- ARIA labels for interactive components

## Performance Considerations

- Lazy loading of analytics components
- Pagination for large networks
- Caching of simulation results
- Throttled graph rendering for smooth interactions