import React from 'react'
import './ErrorResults.css'

function ErrorResults({formResult = {}}) {
    return (
        <div className="row">
            <div className="col-12">
                {formResult.error && (
                    <p className="background-danger">
                        {formResult.error.timestamp} {formResult.error.uniqueId} {formResult.error.message}
                    </p>
                )}

                {formResult.globalErrors && formResult.globalErrors.length > 0 && (
                    <ul>
                        {formResult.globalErrors.map((item, index) => (
                            <li className="bg-danger" key={index}>{item}</li>
                        ))}
                    </ul>
                )}

                {formResult.globalWarnings && formResult.globalWarnings.length > 0 && (
                    <ul>
                        {formResult.globalWarnings.map((item, index) => (
                            <li className="bg-warning" key={index}>{item}</li>
                        ))}
                    </ul>
                )}
            </div>
        </div>
    )
}

export default ErrorResults
