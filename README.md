# BorsaHalal - Islamic Stock Portfolio Management

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-blue.svg)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.5.10-brightgreen.svg)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)](LICENSE)

A cross-platform Islamic stock portfolio management application built with Compose Multiplatform, providing FIFO-based profit calculation, Zakat tracking, and comprehensive portfolio analytics.

## Features

- **Multi-Profile Support**: Manage multiple investment portfolios with isolated data
- **FIFO Profit Calculation**: Accurate First-In-First-Out profit tracking
- **Zakat Management**: Track Zakat obligations per stock with customizable percentages
- **Transaction Management**: Record buy/sell transactions with commission tracking
- **Portfolio Analytics**: Real-time portfolio value and performance metrics
- **Offline-First**: All data stored locally using Room database
- **Cross-Platform**: Works on Android, iOS, Desktop (Windows, macOS, Linux)

## Tech Stack

- **Framework**: Compose Multiplatform
- **Language**: Kotlin 1.9.20
- **Database**: Room (SQLite)
- **Architecture**: MVVM + Repository Pattern
- **Dependency Injection**: Koin
- **State Management**: Kotlin StateFlow & Compose State
- **UI**: Material3 Design

## Project Structure

```
BorsaHalal/
├── shared/                           # Shared module (common code)
│   └── src/
│       ├── commonMain/
│       │   └── kotlin/com/borsahalal/
│       │       ├── data/
│       │       │   ├── database/    # Room entities, DAOs, database
│       │       │   └── repository/   # Repository implementations
│       │       ├── domain/          # Business logic (future)
│       │       ├── presentation/
│       │       │   ├── viewmodels/  # ViewModels
│       │       │   └── ui/          # Compose UI screens
│       │       ├── utils/           # Utilities (FIFO calculator, etc.)
│       │       ├── di/              # Koin modules
│       │       └── App.kt           # Main app composable
│       ├── androidMain/
│       ├── iosMain/
│       └── desktopMain/
├── androidApp/                      # Android-specific code
└── iosApp/                         # iOS-specific code (future)
```

## Database Schema

### Entities

1. **Profile**: User portfolios with currency settings
2. **Stock**: Stock information with Zakat percentage
3. **Transaction**: Buy/Sell transactions with commissions
4. **StockHolding**: FIFO holdings tracking
5. **SaleAllocation**: Profit allocation for sales

## Getting Started

### Prerequisites

- JDK 11 or higher
- Android Studio Hedgehog or later
- Xcode 14+ (for iOS development)
- Gradle 8.4+

### Building

#### Android
```bash
./gradlew :androidApp:assembleDebug
```

#### iOS
```bash
./gradlew :iosApp:buildXCFramework
```

#### Desktop
```bash
./gradlew :desktopApp:run
```

## Development Roadmap

### Phase 1: Core Infrastructure ✅
- [x] Project setup
- [x] Database schema
- [x] Repository layer
- [x] Dependency injection
- [x] Theme system

### Phase 2: Basic Features (In Progress)
- [ ] Profile management UI
- [ ] Stock CRUD operations
- [ ] Transaction recording
- [ ] Dashboard implementation
- [ ] FIFO calculation integration

### Phase 3: Advanced Features
- [ ] Portfolio analytics
- [ ] Zakat calculations
- [ ] Reports generation
- [ ] Data export (CSV, Excel, PDF)
- [ ] Multi-language support

### Phase 4: Polish
- [ ] Performance optimization
- [ ] Unit & integration tests
- [ ] Error handling
- [ ] Data backup/restore

## Key Features Explained

### FIFO Profit Calculation

The app uses a sophisticated FIFO (First-In-First-Out) algorithm to calculate profits:

1. When you sell stocks, the app matches the sale against your oldest purchases first
2. Profit is calculated per buy-sell pair
3. Commissions are factored into net profit calculations
4. All allocations are tracked in the database for transparency

### Zakat Tracking

- Customizable Zakat percentage per stock (default 2.5%)
- Annual Zakat calculations based on current portfolio value
- Supports different Zakat calculation methods

### Multi-Profile Support

- Create multiple portfolios (e.g., personal, family, business)
- Each profile has isolated data
- Switch between profiles seamlessly
- Different currency support per profile

## Architecture

The app follows Clean Architecture principles with three main layers:

1. **Data Layer**: Database, DAOs, and repositories
2. **Domain Layer**: Business logic and use cases
3. **Presentation Layer**: ViewModels and Compose UI

## Contributing

This is a proprietary project. Contributions are currently not accepted.

## License

All rights reserved. See [LICENSE](LICENSE) file for details.

## Support

For questions or support, contact: support@borsahalal.com

## Acknowledgments

- Built with [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- Database powered by [Room](https://developer.android.com/training/data-storage/room)
- Dependency injection by [Koin](https://insert-koin.io/)

---

**Version**: 1.0.0
**Last Updated**: November 2024
