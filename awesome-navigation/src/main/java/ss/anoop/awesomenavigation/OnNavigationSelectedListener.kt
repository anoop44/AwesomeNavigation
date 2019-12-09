package ss.anoop.awesomenavigation

interface OnNavigationSelectedListener {

    fun onSelectNavigation(
        id: Int,
        position: Int
    )

    fun onReselectNavigation(
        id: Int,
        position: Int
    )
}