import React, { useEffect, useState } from 'react'

const API = 'http://localhost:8080'

export default function App() {
  const [token, setToken] = useState(localStorage.getItem('token'))
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [tasks, setTasks] = useState([])
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [error, setError] = useState(null)
  const [roles, setRoles] = useState([])

  useEffect(() => {
    if (token) {
      parseRolesFromToken()
      fetchTasks()
    }
  }, [token])

  function parseRolesFromToken() {
    try {
      const parts = token.split('.')
      if (parts.length < 2) return
      const payload = JSON.parse(atob(parts[1].replace(/-/g, '+').replace(/_/g, '/')))
      setRoles(payload.roles || [])
    } catch (e) { /* ignore */ }
  }

  async function login(e) {
    e.preventDefault()
    const res = await fetch(`${API}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    })
    if (!res.ok) {
      setError('Login failed')
      return
    }
    const body = await res.json()
    localStorage.setItem('token', body.token)
    setToken(body.token)
  }

  async function fetchTasks() {
    const res = await fetch(`${API}/api/tasks`, { headers: { Authorization: `Bearer ${token}` } })
    if (!res.ok) { setError('Failed to load tasks'); return }
    setError(null)
    setTasks(await res.json())
  }

  async function createTask(e) {
    e.preventDefault()
    const res = await fetch(`${API}/api/tasks`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
      body: JSON.stringify({ title, description })
    })
    if (!res.ok) { setError('Create failed'); return }
    setTitle('')
    setDescription('')
    fetchTasks()
  }

  async function assignTask(id, assignee) {
    setError(null)
    const res = await fetch(`${API}/api/tasks/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
      body: JSON.stringify({ assignee })
    })
    if (!res.ok) { setError('Assign failed'); return }
    fetchTasks()
  }

  function logout() {
    localStorage.removeItem('token')
    setToken(null)
  }

  if (!token) {
    return (
      <div className="container">
        <h2>Login</h2>
        {error && <div style={{color:'red'}}>{error}</div>}
        <form onSubmit={login}>
          <input placeholder="username" value={username} onChange={e => setUsername(e.target.value)} />
          <input placeholder="password" type="password" value={password} onChange={e => setPassword(e.target.value)} />
          <button type="submit">Login</button>
        </form>
        <p>Use <strong>admin/admin</strong> for demo.</p>
      </div>
    )
  }

  return (
    <div className="container">
      <h2>Tasks</h2>
        <div style={{display:'flex', justifyContent:'space-between', alignItems:'center'}}>
          <div>Roles: {roles.join(', ') || 'none'}</div>
          <button onClick={logout}>Logout</button>
        </div>
        {error && <div style={{color:'red'}}>{error}</div>}
      <form onSubmit={createTask} className="task-form">
        <input placeholder="title" value={title} onChange={e => setTitle(e.target.value)} />
        <input placeholder="description" value={description} onChange={e => setDescription(e.target.value)} />
        <button type="submit">Create</button>
      </form>
      <ul>
          {tasks.map(t => (
            <li key={t.id}>
              <strong>{t.title}</strong> — {t.description}
              <div>Assignee: {t.assignee || 'unassigned'}</div>
              <div style={{display:'flex', gap:8, marginTop:6}}>
                <input placeholder="assignee" id={`assignee-${t.id}`} style={{flex:1}} />
                <button onClick={() => {
                  const val = document.getElementById(`assignee-${t.id}`).value
                  assignTask(t.id, val)
                }}>Assign</button>
              </div>
            </li>
          ))}
      </ul>
    </div>
  )
}
