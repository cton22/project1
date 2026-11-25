import { useEffect, useState } from 'react'
import { Activity, Camera, RefreshCw, Plus, Monitor, Database, ShieldCheck } from 'lucide-react'

const API = 'http://localhost:8080/api'

export default function App() {
  const [accounts, setAccounts] = useState([])
  const [snapshots, setSnapshots] = useState([])
  const [selected, setSelected] = useState('')
  const [newName, setNewName] = useState('')
  const [busy, setBusy] = useState(false)
  const [message, setMessage] = useState('')
  const [health, setHealth] = useState('Checking')

  async function load() {
    try {
      const [a, s, h] = await Promise.all([
        fetch(`${API}/accounts`).then(r => r.json()),
        fetch(`${API}/snapshots`).then(r => r.json()),
        fetch(`${API}/health`).then(r => r.json())
      ])
      setAccounts(a)
      setSnapshots(s)
      setHealth(h.status)
      if (!selected && a.length) setSelected(String(a[0].id))
    } catch {
      setHealth('Offline')
      setMessage('Backend unavailable. Start Spring Boot on port 8080.')
    }
  }

  useEffect(() => { load() }, [])

  async function addAccount(e) {
    e.preventDefault()
    if (!newName.trim()) return
    const response = await fetch(`${API}/accounts`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name: newName.trim() })
    })
    const data = await response.json()
    if (!response.ok) setMessage(data.error || 'Could not create profile')
    else {
      setNewName('')
      setMessage('Profile created')
      await load()
      setSelected(String(data.id))
    }
  }

  async function capture() {
    if (!selected) {
      setMessage('Create or select a profile first')
      return
    }
    setBusy(true)
    setMessage('Capturing primary display and running OCR...')
    try {
      const response = await fetch(`${API}/monitor/capture`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ accountId: Number(selected), runOcr: true })
      })
      const data = await response.json()
      if (!response.ok) throw new Error(data.error || 'Capture failed')
      setMessage(data.ocrAvailable ? 'Capture and OCR completed' : 'Screenshot saved; OCR unavailable or skipped')
      await load()
    } catch (e) {
      setMessage(e.message)
    } finally {
      setBusy(false)
    }
  }

  const latest = snapshots[0]

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand"><div className="brand-icon">R</div><div><b>RoK Monitor</b><small>LOCAL CONTROL CENTER</small></div></div>
        <div className="nav-label">WORKSPACE</div>
        <div className="nav-item active"><Activity size={17}/> Overview</div>
        <div className="nav-item"><Monitor size={17}/> Screen Monitor</div>
        <div className="nav-item"><Database size={17}/> History</div>
        <div className="sidebar-bottom"><ShieldCheck size={17}/> Read-only monitoring</div>
      </aside>

      <main className="main">
        <header className="topbar">
          <div><span className="eyebrow">RISE OF KINGDOMS / LOCAL MONITOR</span><h1>Overview</h1></div>
          <div className={`status ${health === 'UP' ? 'online' : 'offline'}`}><span/> Backend {health}</div>
        </header>

        <section className="welcome">
          <div><div className="eyebrow light">MONITORING WORKSPACE</div><h2>Your kingdom, at a glance.</h2><p>Capture the visible game screen, inspect OCR output and keep a local history.</p></div>
          <div className="welcome-mark">ROK</div>
        </section>

        {message && <div className="notice">{message}</div>}

        <section className="stats-grid">
          <div className="stat-card"><div className="stat-head">Saved snapshots <Camera size={18}/></div><strong>{snapshots.length}</strong><small>Recent records in local database</small></div>
          <div className="stat-card"><div className="stat-head">Profiles <Monitor size={18}/></div><strong>{accounts.length}</strong><small>Labels for organizing observations</small></div>
          <div className="stat-card"><div className="stat-head">Monitor mode <ShieldCheck size={18}/></div><strong className="mode">Read-only</strong><small>No game inputs are sent</small></div>
        </section>

        <section className="content-grid">
          <div className="panel">
            <div className="panel-title"><div><h3>Screen monitor</h3><p>Capture the primary Windows display</p></div><Camera size={20}/></div>
            <label>Account profile</label>
            <select value={selected} onChange={e => setSelected(e.target.value)}>
              <option value="">Select profile</option>
              {accounts.map(a => <option key={a.id} value={a.id}>{a.name}</option>)}
            </select>
            <button className="primary" onClick={capture} disabled={busy || health !== 'UP'}>
              {busy ? <RefreshCw className="spin" size={17}/> : <Camera size={17}/>}
              {busy ? 'Working...' : 'Capture & analyze'}
            </button>
            <form className="add-form" onSubmit={addAccount}>
              <label>Create profile</label>
              <div className="inline"><input value={newName} onChange={e => setNewName(e.target.value)} placeholder="e.g. Main / Farm 01" maxLength="80"/><button className="icon-button" aria-label="Add profile"><Plus size={18}/></button></div>
            </form>
            <div className="hint">The capture includes whatever is visible on your primary display. Keep the game window visible.</div>
          </div>

          <div className="panel">
            <div className="panel-title"><div><h3>Latest capture</h3><p>{latest ? new Date(latest.capturedAt).toLocaleString() : 'No captures yet'}</p></div><Activity size={20}/></div>
            {latest ? <>
              <img className="screenshot" src={`${API}${latest.imageUrl}`} alt="Latest desktop capture"/>
              <div className="capture-meta"><b>{latest.accountName}</b><span>{latest.ocrAvailable ? 'OCR completed' : 'OCR unavailable'}</span></div>
              <pre className="ocr-text">{latest.ocrText || 'No OCR text available for this capture.'}</pre>
            </> : <div className="empty"><Camera size={30}/><b>Nothing captured yet</b><span>Start a capture to see your screenshot here.</span></div>}
          </div>
        </section>

        <section className="panel history">
          <div className="panel-title"><div><h3>Recent activity</h3><p>Latest local observations</p></div><button className="refresh" onClick={load}><RefreshCw size={15}/> Refresh</button></div>
          {snapshots.length ? <div className="table-wrap"><table><thead><tr><th>Profile</th><th>Captured at</th><th>Screen type</th><th>OCR</th></tr></thead><tbody>
            {snapshots.slice(0, 10).map(s => <tr key={s.id}><td>{s.accountName}</td><td>{new Date(s.capturedAt).toLocaleString()}</td><td>{s.screenType}</td><td><span className={s.ocrAvailable ? 'pill good' : 'pill'}>{s.ocrAvailable ? 'Available' : 'Unavailable'}</span></td></tr>)}
          </tbody></table></div> : <div className="empty small">No history available.</div>}
        </section>
        <footer>RoK Monitor · Local-first · SQLite · Java 21 + React</footer>
      </main>
    </div>
  )
}
