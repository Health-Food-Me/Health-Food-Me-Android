package org.helfoome.presentation.type

import org.helfoome.R

enum class AlertType(val dialogTitle: Int, val dialogDescription: Int) {
    EDIT_CANCEL(R.string.my_edit_dialog_caution, R.string.my_edit_dialog_caution_description),
    WRITE_CANCEL(R.string.my_review_write_dialog_caution, R.string.my_review_write_dialog_caution_description),
    LOGOUT(R.string.logout_dialog_caution, R.string.logout_dialog_caution_description),
    DELETE_REVIEW(R.string.my_review_dialog_caution, 0),
}
