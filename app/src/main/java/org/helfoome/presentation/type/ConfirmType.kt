package org.helfoome.presentation.type

import org.helfoome.R

enum class ConfirmType(val dialogTitle: Int, val dialogDescription: Int) {
    NETWORK_CONFIRM(R.string.confirm_dialog_network_caution,0),
    LOGIN_CONFIRM(R.string.confirm_dialog_login_caution, 0),
    LOCATION_CONFIRM(R.string.confirm_dialog_location_caution, R.string.confirm_dialog_location_caution_description)
}