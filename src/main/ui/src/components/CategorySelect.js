import React, {useEffect, useRef, useState} from 'react'
import {get} from '../utils/http'
import './CategorySelect.css'

function CategorySelect({id = 'categorySelect', value = '', onChange}) {
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
            const response = await get('/api/item/listCategories')
            const categories = response.data || []
            
            // Filter categories based on search term if provided
            const filteredCategories = searchFor 
                ? categories.filter(category => 
                    category.toLowerCase().includes(searchFor.toLowerCase()))
                : categories
                
            setItems(filteredCategories)
            console.log('Categories - Loaded', filteredCategories.length, 'items for', searchFor)
        } catch (error) {
            console.error('Error loading categories', error)
            setItems([])
        }
    }

    const handleInputChange = (event) => {
        const newValue = event.target.value
        setInputValue(newValue)
        search(newValue)
    }

    const handleSelected = (category) => {
        console.log('Selected', category)
        setInputValue(category)
        if (onChange) {
            onChange(category)
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
                    {items.map(category => (
                        <li key={category} onClick={() => handleSelected(category)}>
                            {category}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    )
}

export default CategorySelect