import React, {useState} from 'react'
import ErrorResults from '../../components/ErrorResults'
import service, {showSuccess} from '../../service'
import {t} from '../../utils/TranslationUtils'

function AccountChangePassword({appDetails = {}, onAppDetailsChange}) {
    const [changePasswordForm, setChangePasswordForm] = useState({})
    const [formResult, setFormResult] = useState({})

    const handleChangePasswordFormChange = (field, value) => {
        setChangePasswordForm(prev => ({
            ...prev,
            [field]: value
        }))
    }

    const changePassword = async () => {
        setFormResult({})
        try {
            const response = await service.userChangePassword(changePasswordForm)
            setFormResult(response.data)
            if (response.data.success) {
                setChangePasswordForm({})
                showSuccess(t('account.changePassword.success'))
                if (onAppDetailsChange) {
                    onAppDetailsChange()
                }
            }
        } catch (error) {
            console.error('Error changing password', error)
        }
    }

    return (
        <div className="row">
            <div className="col-12">
                <ErrorResults formResult={formResult}/>

                <div className="col-12 col-md-6 col-lg-4">
                    {appDetails.userHasPassword && (
                        <div className="mb-3">
                            <label htmlFor="currentPassword">{t('account.currentPassword')}</label>
                            <input
                                type="password"
                                className="form-control"
                                id="currentPassword"
                                value={changePasswordForm.currentPassword || ''}
                                onChange={(e) => handleChangePasswordFormChange('currentPassword', e.target.value)}
                                autoComplete="current-password"
                            />
                            {formResult.validationErrorsByField && formResult.validationErrorsByField.currentPassword && (
                                <div className="text-danger">
                                    {formResult.validationErrorsByField.currentPassword.map((errorCode, index) => (
                                        <p key={index}>{t(errorCode)}</p>
                                    ))}
                                </div>
                            )}
                        </div>
                    )}

                    <div className="mb-3">
                        <label htmlFor="newPassword">{t('account.newPassword')}</label>
                        <input
                            type="password"
                            className="form-control"
                            id="newPassword"
                            value={changePasswordForm.newPassword || ''}
                            onChange={(e) => handleChangePasswordFormChange('newPassword', e.target.value)}
                            autoComplete="new-password"
                        />
                        {formResult.validationErrorsByField && formResult.validationErrorsByField.newPassword && (
                            <div className="text-danger">
                                {formResult.validationErrorsByField.newPassword.map((errorCode, index) => (
                                    <p key={index}>{t(errorCode)}</p>
                                ))}
                            </div>
                        )}
                    </div>

                    <div className="mb-3">
                        <label htmlFor="newPasswordConfirmation">{t('account.newPasswordConfirmation')}</label>
                        <input
                            type="password"
                            className="form-control"
                            id="newPasswordConfirmation"
                            value={changePasswordForm.newPasswordConfirmation || ''}
                            onChange={(e) => handleChangePasswordFormChange('newPasswordConfirmation', e.target.value)}
                            autoComplete="new-password"
                        />
                        {formResult.validationErrorsByField && formResult.validationErrorsByField.newPasswordConfirmation && (
                            <div className="text-danger">
                                {formResult.validationErrorsByField.newPasswordConfirmation.map((errorCode, index) => (
                                    <p key={index}>{t(errorCode)}</p>
                                ))}
                            </div>
                        )}
                    </div>

                    <button type="button" className="btn btn-primary" onClick={changePassword}>
                        {t('account.changePassword')}
                    </button>
                </div>
            </div>
        </div>
    )
}

export default AccountChangePassword
