# Mercor Take Home - Referral Network System

A comprehensive referral network management system with both Java backend implementation and React frontend interface for analyzing and optimizing employee referral programs.

## 📁 Project Structure

```
Mercor Take Home/
├── source/                          # Java backend implementation
│   ├── ReferralNetwork.java         # Core network data structure
│   ├── NetworkGrowthSimulation.java # Growth simulation algorithms
│   └── *.class                      # Compiled Java classes
├── tests/                           # Java unit tests
│   ├── ReferralNetworkTest.java     # Network functionality tests
│   ├── NetworkGrowthSimulationTest.java # Simulation tests
│   └── *.class                      # Compiled test classes
├── referral-network-ui/             # React frontend application
│   ├── src/                         # Source code
│   ├── public/                      # Static assets
│   ├── package.json                 # Node.js dependencies
│   └── README.md                    # Frontend documentation
├── junit-platform-console-standalone-1.10.0.jar # JUnit testing framework
├── UI_MOCKUP.md                     # UI design specifications
└── README.md                        # This file
```

## 🎯 System Overview

This referral network system provides comprehensive tools for:

- **Network Management**: Add, validate, and query referral relationships
- **Analytics**: Calculate reach metrics, identify top performers, and network brokers
- **Simulation**: Model network growth and optimize referral bonus strategies
- **Visualization**: Interactive network graphs and analytics dashboards

## 🚀 Quick Start

### Backend (Java)

```bash
# Compile Java sources
javac -cp ".:junit-platform-console-standalone-1.10.0.jar" source/*.java tests/*.java

# Run tests
java -jar junit-platform-console-standalone-1.10.0.jar --class-path ".:source:tests" --select-package tests

# Run network simulation
java -cp source NetworkGrowthSimulation
```

### Frontend (React)

```bash
cd referral-network-ui
npm install
npm run dev
```

Open http://localhost:5173 in your browser.

## 🏗️ Architecture

### Java Backend Components

#### ReferralNetwork.java

- **Core Data Structure**: Manages referral graph using adjacency lists
- **Key Methods**:
  - `addReferral(referrer, candidate)` - Add new referral relationships
  - `getDirectReferrals(user)` - Get immediate referrals
  - `getTotalReferralCount(user)` - BFS-based total reach calculation
  - `getTopReferrersByReach(k)` - Ranking by total network reach
  - `getUniqueReachExpansion(k)` - Greedy algorithm for coverage optimization
  - `getFlowCentrality(k)` - Network broker identification

#### NetworkGrowthSimulation.java

- **Growth Modeling**: Monte Carlo simulation of network expansion
- **Optimization**: Bonus amount optimization for target achievements
- **Key Methods**:
  - `simulate(probability, days)` - Run growth simulation
  - `daysToTarget(probability, target)` - Time-to-target calculation
  - `adoptionProb(bonus)` - Bonus-to-probability mapping

### React Frontend Components

#### ReferralNetworkManager

- Primary interface for network management
- Tabs: Add Referral, Search User, Network Graph, Analytics
- Real-time validation and feedback

#### NetworkVisualization

- D3.js force-directed network graph
- Interactive drag, zoom, and pan
- Node highlighting and tooltips

#### NetworkSimulation

- Growth prediction and modeling
- Interactive parameter adjustment
- Recharts-based visualization

#### NetworkTester

- Comprehensive automated testing suite
- 8 test cases covering all major functionality
- Real-time test execution and reporting

## 🔧 Key Features

### Network Management

- ✅ Referral relationship validation
- ✅ Cycle detection and prevention
- ✅ Single-referrer constraint enforcement
- ✅ Real-time network statistics

### Analytics & Insights

- 📊 Top referrers by total reach
- 🎯 Unique reach expansion optimization
- 🔄 Flow centrality analysis
- 📈 Network density metrics

### Growth Simulation

- ⏱️ Time-to-target calculations
- 💰 Bonus optimization algorithms
- 📊 Multi-parameter modeling
- 📈 Interactive visualization

### User Experience

- 🎨 Modern, responsive design
- ⚡ Real-time updates and validation
- 🔍 Interactive network exploration
- 🧪 Built-in testing interface

## 📊 Algorithm Implementations

### Breadth-First Search (BFS)

Used for total referral count calculation and reachability analysis.

### Greedy Algorithm

Implemented for unique reach expansion to maximize network coverage.

### Cycle Detection

Prevents circular referrals using graph traversal algorithms.

### Monte Carlo Simulation

Models network growth under probabilistic referral behavior.

### Force-Directed Layout

Visualizes network structure using D3.js physics simulation.

## 🧪 Testing

### Java Tests

- Unit tests for all core functionality
- JUnit 5 framework integration
- Comprehensive edge case coverage

### Frontend Tests

- Built-in testing interface
- Real-time validation
- Algorithm correctness verification

## 📱 UI Features

### Responsive Design

- Desktop, tablet, and mobile support
- Adaptive layouts and interactions
- Touch-friendly controls

### Interactive Visualization

- Zoom, pan, and drag network nodes
- Real-time highlighting and filtering
- Contextual tooltips and information

### Modern UX

- Smooth animations and transitions
- Loading states and progress indicators
- Comprehensive error handling

## 🔮 Advanced Features

### Network Analytics

- **Top Referrers**: Ranked by total network reach
- **Unique Coverage**: Greedy algorithm for optimal referrer selection
- **Flow Centrality**: Identifies critical network brokers
- **Growth Metrics**: Network density and expansion rates

### Simulation Capabilities

- **Probability Modeling**: Configurable referral success rates
- **Time Projections**: Days to reach target referrals
- **Bonus Optimization**: Cost-effective incentive strategies
- **Growth Visualization**: Interactive charts and projections

### Developer Experience

- **Error Boundaries**: Graceful error handling
- **Hot Reloading**: Instant development feedback
- **Type Safety**: Comprehensive validation
- **Documentation**: Extensive code comments

## 📈 Performance Considerations

### Backend Optimizations

- Efficient graph algorithms (O(V+E) complexity)
- Optimized data structures
- Memory-efficient implementations

### Frontend Optimizations

- Component memoization
- Lazy loading
- Efficient rendering strategies
- Minimal re-computations

## 🎨 Design System

### Color Palette

- Primary: Blue gradient (#3b82f6 to #1e40af)
- Success: Green (#10b981)
- Warning: Orange (#f59e0b)
- Error: Red (#ef4444)

### Typography

- Font: Inter, system fonts
- Consistent sizing and spacing
- High contrast for accessibility

### Components

- Consistent button styles
- Card-based layouts
- Smooth hover states
- Loading animations

## 📚 Documentation

- **UI_MOCKUP.md**: Detailed UI specifications
- **Frontend README**: React application documentation
- **Inline Comments**: Comprehensive code documentation
- **API Documentation**: Method signatures and usage

## 🤝 Development Workflow

1. **Backend Development**: Implement core algorithms in Java
2. **Testing**: Comprehensive unit test coverage
3. **Frontend Development**: React implementation of equivalent functionality
4. **Integration**: Connect frontend with backend algorithms
5. **Testing**: End-to-end validation and user testing
6. **Documentation**: Comprehensive documentation and examples

## 🔍 Project Highlights

### Technical Excellence

- Clean, maintainable code architecture
- Comprehensive error handling
- Performance optimization
- Extensive testing coverage

### User Experience

- Intuitive interface design
- Real-time feedback and validation
- Interactive data visualization
- Responsive across all devices

### Algorithm Implementation

- Efficient graph algorithms
- Advanced analytics capabilities
- Growth simulation and optimization
- Comprehensive validation

## 📞 Support

This project demonstrates advanced software development capabilities including:

- Full-stack development (Java + React)
- Algorithm design and implementation
- Data visualization and user experience
- Testing and validation strategies
- Modern development practices

---

**Note**: This implementation provides both backend Java functionality and a complete frontend interface, demonstrating end-to-end system development for referral network management and analysis.
