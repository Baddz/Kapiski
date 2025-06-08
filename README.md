# Streaming Analytics

A Python tool to analyze and visualize streaming statistics from various Twitch and YouTube chess channels.

## Features

- Data collection from Twitch and YouTube channels
- Statistical analysis of streaming patterns
- Visualization of viewer counts and engagement metrics
- Support for channels like chess.com, chesscomfr, blitzstream, GothamChess, and Naroditsky

## Setup

1. Clone the repository
2. Install dependencies:
```bash
poetry install
```
3. Create a `.env` file with your API keys:
```bash
TWITCH_CLIENT_ID=your_client_id
TWITCH_CLIENT_SECRET=your_client_secret
YOUTUBE_API_KEY=your_api_key
```

## Project Structure

- `streaming_analytics/`: Main package directory
  - `data/`: Data storage and management
  - `scrapers/`: Web scraping and API interaction modules
  - `utils/`: Utility functions and helpers
  - `visualizations/`: Data visualization modules
- `tests/`: Test files 