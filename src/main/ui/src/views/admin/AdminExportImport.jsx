import React, {useRef, useState} from 'react'
import ErrorResults from '../../components/ErrorResults'
import service, {showSuccess} from '../../service'
import {t} from '../../utils/TranslationUtils'

function AdminExportImport() {
    const [formResult, setFormResult] = useState({})
    const fileInputRef = useRef(null)

    // Function to export all the data as a downloadable JSON file
    const exportData = async () => {
        setFormResult({})
        console.log('Admin - Export')

        try {
            const response = await service.adminExport()
            const dataStr = JSON.stringify(response.data.item, null, 2)
            const blob = new Blob([dataStr], {type: 'application/json'})
            const url = URL.createObjectURL(blob)

            const timestamp = new Date().toISOString().replace(/[:.]/g, '-')
            const link = document.createElement('a')
            link.href = url
            link.download = `foilen-crm-export-${timestamp}.json`
            document.body.appendChild(link)
            link.click()
            document.body.removeChild(link)
            URL.revokeObjectURL(url)

            showSuccess(t('prompt.export.success'))
        } catch (error) {
            console.error('Error exporting data', error)
        }
    }

    // Function to trigger the hidden file input
    const triggerImport = () => {
        fileInputRef.current?.click()
    }

    // Function to import all the data from a previously exported JSON file
    const importData = async (event) => {
        const file = event.target.files[0]
        event.target.value = null
        if (!file) {
            return
        }

        if (!window.confirm(t('prompt.import.confirm'))) {
            return
        }

        setFormResult({})
        console.log('Admin - Import', file.name)

        try {
            const text = await file.text()
            const adminExport = JSON.parse(text)
            const response = await service.adminImport(adminExport)
            setFormResult(response.data)
            if (response.data.success) {
                showSuccess(t('prompt.import.success'))
            }
        } catch (error) {
            console.error('Error importing data', error)
        }
    }

    return (
        <div className="row">
            <div className="col-12">
                <ErrorResults formResult={formResult}/>

                <button type="button" className="btn btn-primary me-2" onClick={exportData}>
                    {t('button.export')}
                </button>

                <button type="button" className="btn btn-warning" onClick={triggerImport}>
                    {t('button.import')}
                </button>
                <input
                    type="file"
                    accept="application/json"
                    ref={fileInputRef}
                    style={{display: 'none'}}
                    onChange={importData}
                />
            </div>
        </div>
    )
}

export default AdminExportImport
