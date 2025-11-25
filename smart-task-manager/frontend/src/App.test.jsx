import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import App from './App'
import React from 'react'

describe('App', () => {
  it('renders login when not authenticated', () => {
    // clear token
    localStorage.removeItem('token')
    render(<App />)
    expect(screen.getByText(/Login/i)).toBeDefined()
  })
})
