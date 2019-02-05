package com.airbnb.epoxy

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.ServiceConnection
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.UserHandle
import android.view.ActionMode
import android.view.ContextMenu
import android.view.DragAndDropPermissions
import android.view.DragEvent
import android.view.Menu
import android.view.MenuItem
import android.view.SearchEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.airbnb.epoxy.InteractionManager.InteractionReporter.DuplicateStrategy.KeepLast
import java.io.InputStream

class InteractionTestActivity : FragmentActivity(), InteractionManager.InteractionReporter {
    override var interactionManager: InteractionManager? = null

    private val interactionTester = ActivityInteractionTester(
        activity = this,
        resetViewCallback = ::resetView,
        entryPointProvider = {
            this.window.decorView
        },
        interactionReporters = listOf(
            GlobalLayoutChangeListener(this),
            FragmentChangeListener(this)
        ),
        onEnd = {}
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resetView()
    }

    fun resetView() {
        // Clear all fragments
//        supportFragmentManager.popBackStack()

        // Add original fragment

        // wait for stable ui

        interactionTester.resume()
    }

    fun onFragmentStable() {
    }

    override fun getSupportFragmentManager(): FragmentManager {
        // possibly override and return a no-op manager to make recording changes easier and
        // resetting the view easy
        return super.getSupportFragmentManager()
    }

    override fun startNextMatchingActivity(intent: Intent): Boolean {
        return super.startNextMatchingActivity(intent)
    }

    override fun startNextMatchingActivity(intent: Intent, options: Bundle?): Boolean {
        return super.startNextMatchingActivity(intent, options)
    }

    override fun startActivityFromFragment(fragment: Fragment?, intent: Intent?, requestCode: Int) {
        super.startActivityFromFragment(fragment, intent, requestCode)
    }

    override fun startActivityFromFragment(
        fragment: Fragment?,
        intent: Intent?,
        requestCode: Int,
        options: Bundle?
    ) {
        super.startActivityFromFragment(fragment, intent, requestCode, options)
    }

    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
    override fun startActivityFromFragment(
        fragment: android.app.Fragment,
        intent: Intent?,
        requestCode: Int
    ) {
        super.startActivityFromFragment(fragment, intent, requestCode)
    }

    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
    override fun startActivityFromFragment(
        fragment: android.app.Fragment,
        intent: Intent?,
        requestCode: Int,
        options: Bundle?
    ) {
        super.startActivityFromFragment(fragment, intent, requestCode, options)
    }

    override fun startActionMode(callback: ActionMode.Callback?): ActionMode? {
        return super.startActionMode(callback)
    }

    override fun startActionMode(callback: ActionMode.Callback?, type: Int): ActionMode? {
        return super.startActionMode(callback, type)
    }

    override fun startActivityFromChild(child: Activity, intent: Intent?, requestCode: Int) {
        super.startActivityFromChild(child, intent, requestCode)
    }

    override fun startActivityFromChild(
        child: Activity,
        intent: Intent?,
        requestCode: Int,
        options: Bundle?
    ) {
        super.startActivityFromChild(child, intent, requestCode, options)
    }

    override fun startActivities(intents: Array<out Intent>?) {
        super.startActivities(intents)
    }

    override fun startActivities(intents: Array<out Intent>?, options: Bundle?) {
        super.startActivities(intents, options)
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
    }

    override fun startActivity(intent: Intent?, options: Bundle?) {
        super.startActivity(intent, options)
    }

    override fun startIntentSenderFromChild(
        child: Activity?,
        intent: IntentSender?,
        requestCode: Int,
        fillInIntent: Intent?,
        flagsMask: Int,
        flagsValues: Int,
        extraFlags: Int
    ) {
        super.startIntentSenderFromChild(
            child,
            intent,
            requestCode,
            fillInIntent,
            flagsMask,
            flagsValues,
            extraFlags
        )
    }

    override fun startIntentSenderFromChild(
        child: Activity?,
        intent: IntentSender?,
        requestCode: Int,
        fillInIntent: Intent?,
        flagsMask: Int,
        flagsValues: Int,
        extraFlags: Int,
        options: Bundle?
    ) {
        super.startIntentSenderFromChild(
            child,
            intent,
            requestCode,
            fillInIntent,
            flagsMask,
            flagsValues,
            extraFlags,
            options
        )
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int, options: Bundle?) {
        super.startActivityForResult(intent, requestCode, options)
    }

    override fun startActivityIfNeeded(intent: Intent, requestCode: Int): Boolean {
        return super.startActivityIfNeeded(intent, requestCode)
    }

    override fun startActivityIfNeeded(
        intent: Intent,
        requestCode: Int,
        options: Bundle?
    ): Boolean {
        return super.startActivityIfNeeded(intent, requestCode, options)
    }

    override fun onNavigateUp(): Boolean {
        return super.onNavigateUp()
    }

    override fun navigateUpTo(upIntent: Intent?): Boolean {
        return super.navigateUpTo(upIntent)
    }

    override fun finish() {
        super.finish()
    }

    override fun finishAffinity() {
        super.finishAffinity()
    }

    override fun finishAfterTransition() {
        super.finishAfterTransition()
    }

    override fun supportFinishAfterTransition() {
        super.supportFinishAfterTransition()
    }

    override fun setFinishOnTouchOutside(finish: Boolean) {
        super.setFinishOnTouchOutside(finish)
    }

    override fun finishAndRemoveTask() {
        super.finishAndRemoveTask()
    }

    override fun finishActivity(requestCode: Int) {
        super.finishActivity(requestCode)
    }

    override fun finishFromChild(child: Activity?) {
        super.finishFromChild(child)
    }

    override fun finishActivityFromChild(child: Activity, requestCode: Int) {
        super.finishActivityFromChild(child, requestCode)
    }

    override fun moveTaskToBack(nonRoot: Boolean): Boolean {
        return super.moveTaskToBack(nonRoot)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem?): Boolean {
        return super.onMenuItemSelected(featureId, item)
    }

    override fun navigateUpToFromChild(child: Activity?, upIntent: Intent?): Boolean {
        return super.navigateUpToFromChild(child, upIntent)
    }

    override fun onNavigateUpFromChild(child: Activity?): Boolean {
        return super.onNavigateUpFromChild(child)
    }

    override fun setActionBar(toolbar: Toolbar?) {
        super.setActionBar(toolbar)
    }

    override fun openContextMenu(view: View?) {
        super.openContextMenu(view)
    }

    override fun startIntentSender(
        intent: IntentSender?,
        fillInIntent: Intent?,
        flagsMask: Int,
        flagsValues: Int,
        extraFlags: Int
    ) {
        super.startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags)
    }

    override fun startIntentSender(
        intent: IntentSender?,
        fillInIntent: Intent?,
        flagsMask: Int,
        flagsValues: Int,
        extraFlags: Int,
        options: Bundle?
    ) {
        super.startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags, options)
    }

    override fun onSearchRequested(searchEvent: SearchEvent?): Boolean {
        return super.onSearchRequested(searchEvent)
    }

    override fun onSearchRequested(): Boolean {
        return super.onSearchRequested()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        return super.onContextItemSelected(item)
    }

    override fun stopService(name: Intent?): Boolean {
        return super.stopService(name)
    }

    override fun onCreateNavigateUpTaskStack(builder: TaskStackBuilder?) {
        super.onCreateNavigateUpTaskStack(builder)
    }

    override fun registerForContextMenu(view: View?) {
        super.registerForContextMenu(view)
    }

    override fun onContextMenuClosed(menu: Menu?) {
        super.onContextMenuClosed(menu)
    }

    override fun setTheme(resid: Int) {
        super.setTheme(resid)
    }

    override fun sendBroadcast(intent: Intent?) {
        super.sendBroadcast(intent)
    }

    override fun sendBroadcast(intent: Intent?, receiverPermission: String?) {
        super.sendBroadcast(intent, receiverPermission)
    }

    @SuppressLint("MissingPermission")
    override fun sendOrderedBroadcastAsUser(
        intent: Intent?,
        user: UserHandle?,
        receiverPermission: String?,
        resultReceiver: BroadcastReceiver?,
        scheduler: Handler?,
        initialCode: Int,
        initialData: String?,
        initialExtras: Bundle?
    ) {
        super.sendOrderedBroadcastAsUser(
            intent,
            user,
            receiverPermission,
            resultReceiver,
            scheduler,
            initialCode,
            initialData,
            initialExtras
        )
    }

    override fun addContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.addContentView(view, params)
    }

    override fun setTaskDescription(taskDescription: ActivityManager.TaskDescription?) {
        super.setTaskDescription(taskDescription)
    }

    override fun deleteFile(name: String?): Boolean {
        return super.deleteFile(name)
    }

    override fun setTurnScreenOn(turnScreenOn: Boolean) {
        super.setTurnScreenOn(turnScreenOn)
    }

    override fun sendOrderedBroadcast(intent: Intent?, receiverPermission: String?) {
        super.sendOrderedBroadcast(intent, receiverPermission)
    }

    override fun sendOrderedBroadcast(
        intent: Intent,
        receiverPermission: String?,
        resultReceiver: BroadcastReceiver?,
        scheduler: Handler?,
        initialCode: Int,
        initialData: String?,
        initialExtras: Bundle?
    ) {
        super.sendOrderedBroadcast(
            intent,
            receiverPermission,
            resultReceiver,
            scheduler,
            initialCode,
            initialData,
            initialExtras
        )
    }

    override fun setVisible(visible: Boolean) {
        super.setVisible(visible)
    }

    override fun unbindService(conn: ServiceConnection) {
        super.unbindService(conn)
    }

    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
    override fun setTitleColor(textColor: Int) {
        super.setTitleColor(textColor)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.setContentView(view, params)
    }

    override fun stopLockTask() {
        super.stopLockTask()
    }

    override fun setImmersive(i: Boolean) {
        super.setImmersive(i)
    }

    override fun shouldUpRecreateTask(targetIntent: Intent?): Boolean {
        return super.shouldUpRecreateTask(targetIntent)
    }

    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
    override fun enterPictureInPictureMode() {
        super.enterPictureInPictureMode()
    }

    override fun enterPictureInPictureMode(params: PictureInPictureParams): Boolean {
        return super.enterPictureInPictureMode(params)
    }

    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
    override fun setWallpaper(bitmap: Bitmap?) {
        super.setWallpaper(bitmap)
    }

    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
    override fun setWallpaper(data: InputStream?) {
        super.setWallpaper(data)
    }

    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
    @SuppressLint("MissingPermission")
    override fun removeStickyBroadcastAsUser(intent: Intent?, user: UserHandle?) {
        super.removeStickyBroadcastAsUser(intent, user)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
    }

    override fun closeContextMenu() {
        super.closeContextMenu()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onMenuOpened(featureId: Int, menu: Menu?): Boolean {
        return super.onMenuOpened(featureId, menu)
    }

    override fun onOptionsMenuClosed(menu: Menu?) {
        super.onOptionsMenuClosed(menu)
    }

    override fun closeOptionsMenu() {
        super.closeOptionsMenu()
    }

    override fun createPendingResult(requestCode: Int, data: Intent, flags: Int): PendingIntent {
        return super.createPendingResult(requestCode, data, flags)
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
    }

    override fun setTitle(titleId: Int) {
        super.setTitle(titleId)
    }

    override fun requestDragAndDropPermissions(event: DragEvent?): DragAndDropPermissions {
        return super.requestDragAndDropPermissions(event)
    }

    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
    override fun requestVisibleBehind(visible: Boolean): Boolean {
        return super.requestVisibleBehind(visible)
    }

    override fun onPrepareNavigateUpTaskStack(builder: TaskStackBuilder?) {
        super.onPrepareNavigateUpTaskStack(builder)
    }

    override fun setIntent(newIntent: Intent?) {
        super.setIntent(newIntent)
    }

    override fun invalidateOptionsMenu() {
        super.invalidateOptionsMenu()
    }
}

class GlobalLayoutChangeListener(
    val activity: FragmentActivity
) : InteractionManager.InteractionReporter, ViewTreeObserver.OnGlobalLayoutListener {
    override var interactionManager: InteractionManager? = null

    override fun onGlobalLayout() {
        reportInteraction(
            interactionName = "GlobalLayoutChange",
            details = "",
            duplicateStrategy = KeepLast
        )
    }
}

class FragmentChangeListener(
    val activity: FragmentActivity
) : InteractionManager.InteractionReporter {
    override var interactionManager: InteractionManager? = null

    private fun onAnyLifecycleEvent() {
        reportInteraction(
            interactionName = "Fragment Change",
            details = "${fragments.map { it.describe() }}",
            duplicateStrategy = KeepLast
        )
    }

    private val fragments: List<Fragment>
        get() = activity.supportFragmentManager.recursiveFragments()

    private fun FragmentManager.recursiveFragments(): List<Fragment> {
        return fragments + fragments.flatMap {
            it.fragmentManager?.recursiveFragments() ?: emptyList()
        }
    }

    private fun Fragment.describe(): String {
        return "${this::class.java.simpleName} : ${parentFragment?.let { it::class.java.simpleName }} : ${lifecycle.currentState} : $arguments"
    }

    init {
        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(object :
            FragmentManager.FragmentLifecycleCallbacks() {

            override fun onFragmentAttached(
                fm: FragmentManager,
                f: Fragment,
                context: Context
            ) {
                onAnyLifecycleEvent()
            }

            override fun onFragmentCreated(
                fm: FragmentManager,
                f: Fragment,
                savedInstanceState: Bundle?
            ) {
                onAnyLifecycleEvent()
            }

            override fun onFragmentViewCreated(
                fm: FragmentManager,
                f: Fragment,
                v: View,
                savedInstanceState: Bundle?
            ) {
                onAnyLifecycleEvent()
            }

            override fun onFragmentPreAttached(
                fm: FragmentManager,
                f: Fragment,
                context: Context
            ) {
                onAnyLifecycleEvent()
            }

            override fun onFragmentSaveInstanceState(
                fm: FragmentManager,
                f: Fragment,
                outState: Bundle
            ) {
                onAnyLifecycleEvent()
            }

            override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
                onAnyLifecycleEvent()
            }

            override fun onFragmentPreCreated(
                fm: FragmentManager,
                f: Fragment,
                savedInstanceState: Bundle?
            ) {
                onAnyLifecycleEvent()
            }

            override fun onFragmentActivityCreated(
                fm: FragmentManager,
                f: Fragment,
                savedInstanceState: Bundle?
            ) {
                onAnyLifecycleEvent()
            }

            override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
                onAnyLifecycleEvent()
            }

            override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                onAnyLifecycleEvent()
            }

            override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                onAnyLifecycleEvent()
            }

            override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
                onAnyLifecycleEvent()
            }

            override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
                onAnyLifecycleEvent()
            }

            override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                onAnyLifecycleEvent()
            }
        }, true)
    }
}