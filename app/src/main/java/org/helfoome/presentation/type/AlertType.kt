package org.helfoome.presentation.type

import org.helfoome.R

enum class AlertType(val strResId: Pair<Int, Int>) {
    EDIT_CANCEL(Pair(R.string.my_edit_dialog_caution, R.string.my_edit_dialog_caution_description)),
    WRITE_CANCEL(Pair(R.string.logout_dialog_caution, R.string.my_review_write_dialog_caution_description)),
    LOGOUT(Pair(R.string.logout_dialog_caution, R.string.logout_dialog_caution_description)),
    DELETE_REVIEW(Pair(R.string.my_review_dialog_caution, 0)),
}
