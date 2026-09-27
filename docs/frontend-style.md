# JavaScript/React (Frontend) Code Style Guidelines

## File Structure

```
src/main/ui/src/
  ├── components/          # Reusable components (e.g. ClientSelect, PaginationControl)
  ├── views/               # Page-level components (with account/ and admin/ subfolders)
  ├── utils/               # Utility functions (translations, lang, feature flags)
  └── service.js           # Axios wrapper + CSRF handling + one method per API endpoint
```

## Imports Ordering

```javascript
// 1. React and hooks
import React, {useEffect, useState} from 'react'
// 2. Third-party libraries (routing, etc.)
import {NavLink, Route} from 'react-router-dom'
// 3. Local components
import ErrorResults from '../components/ErrorResults'
// 4. Service + utilities
import service, {showSuccess} from '../service'
import {t, updateAppDetails} from '../utils/TranslationUtils'
// 5. CSS last
import './App.css'
```

## Naming Conventions

- **Components**: PascalCase (`ClientsList`, `ErrorResults`)
- **Files**: Match component name with `.jsx` extension (`ClientsList.jsx`)
- **Functions**: camelCase (`refresh`, `handleCreateFormChange`)
- **Constants**: camelCase or UPPER_SNAKE_CASE for true constants

## Component Pattern (CRUD)

```javascript
function ResourceList() {
    const [items, setItems] = useState([])
    // pageId is 1-based; pagination shape mirrors the API response
    const [pagination, setPagination] = useState({currentPageUi: 1, totalPages: 1, firstPage: true, lastPage: true})
    const [createForm, setCreateForm] = useState({})
    const [editForm, setEditForm] = useState({})
    const [formResult, setFormResult] = useState({})

    const refresh = async (pageId = 1) => {
        try {
            const response = await service.resourceListAll(pageId)
            setItems(response.data.items || [])
            setPagination(response.data.pagination)
        } catch (error) {
            console.error('Error loading resources', error)
            setItems([])
        }
    }

    useEffect(() => {
        refresh()
    }, [])

    return (/* JSX with modals + table */)
}
```

## Error Handling

```javascript
// Use try/catch for async operations
try {
    const response = await service.resourceCreate(form)
    setFormResult(response.data)
    if (response.data.success) {
        showSuccess(t('prompt.create.success'))
        refresh()
    }
} catch (error) {
    console.error('Error creating resource', error)
}

// Display errors with ErrorResults component
<ErrorResults formResult={formResult}/>
```

## Service / HTTP Layer

- Use the singleton default-exported from `src/main/ui/src/service.js` (not axios directly)
- One method per API endpoint, named `{resource}{Action}` (e.g. `service.clientCreate(form)`,
  `service.clientListAll(pageId, search)`, `service.clientUpdate(shortName, form)`,
  `service.clientDelete(shortName)`) — add a new method there for each new endpoint rather than
  calling a generic `get`/`post`/`put`/`delete`
- Also exports `showSuccess(message)` / `showError(message)` (react-toastify) for user notifications
- CSRF: the axios request interceptor attaches the `XSRF-TOKEN` cookie as an `X-XSRF-TOKEN` header;
  `post`/`put`/`delete` first call `ensureCsrfToken()`, which hits `GET /api/csrf` if the cookie is
  missing (see `CsrfApiController`) — no manual handling needed

## Translation/i18n

```javascript
import {t} from './utils/TranslationUtils'

// Simple translation
{
    t('menu.clients')
}

// With placeholders
{
    t('prompt.create.success', {0: clientName})
}
```
