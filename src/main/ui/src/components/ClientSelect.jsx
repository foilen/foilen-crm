import React, {useEffect, useRef, useState} from 'react'
import {get} from '../utils/http'
import './ClientSelect.css'

function ClientSelect({id = 'clientSelect', value = '', onChange}) {
    const [items, setItems] = useState([])
    const [focused, setFocused] = useState(false)
    const [inputValue, setInputValue] = useState(value)
    const timeoutRef = useRef(null)

    // Update internal state when value prop changes
    useEffect(() => {
        setInputValue(value)
    }, [value])

    // Initial search on mount
    useEffect(() => {
        search()
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [])

    const search = async (searchFor = null) => {
        console.log('Search', searchFor)

        if (onChange && searchFor !== undefined) {
            onChange(searchFor)
        }

        try {
            const response = await get('/api/client/listAll', {search: searchFor})
            setItems(response.data.items || [])
        } catch (error) {
            console.error('Error loading clients', error)
            setItems([])
        }
    }

    const handleInputChange = (event) => {
        const newValue = event.target.value
        setInputValue(newValue)
        search(newValue)
    }

    const handleSelected = (sid) => {
        console.log('Selected', sid)
        setInputValue(sid)
        if (onChange) {
            onChange(sid)
        }
        setFocused(false)
    }

    const handleFocus = () => {
        setFocused(true)
    }

    const handleBlur = () => {
        // Small delay to allow click events on list items to complete before hiding the list
        timeoutRef.current = setTimeout(() => {
            setFocused(false)
        }, 200)
    }

    // Clean up timeout on unmount
    useEffect(() => {
        return () => {
            if (timeoutRef.current) {
                clearTimeout(timeoutRef.current)
            }
        }
    }, [])

    return (
        <div className="typeahead">
            <input
                type="text"
                className="form-control"
                id={id}
                value={inputValue}
                onChange={handleInputChange}
                onFocus={handleFocus}
                onBlur={handleBlur}
                autoComplete="off"
            />
            {focused && (
                <ul>
                    {items.map(item => (
                        <li key={item.shortName} onClick={() => handleSelected(item.shortName)}>
                            {item.name} ({item.email}) ({item.lang}) ({item.shortName})
                        </li>
                    ))}
                </ul>
            )}
        </div>
    )
}

export default ClientSelect
