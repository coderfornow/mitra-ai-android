package com.example.mitraaiapp.accessibility

import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.mitraaiapp.network.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**

A helper object to coordinate:

Gathering current screen context
2. Calling the Orchestrator API with userGoal

3. Executing the returned actions
 */
object AccessibilityOrchestrator {

    // Example default user goal; real apps might dynamically set from UI.
    var userGoal: String = "Book a flight from NYC to London"

    fun handleEvent(service: MyAccessibilityService, event: AccessibilityEvent) {
        val rootNode = service.rootInActiveWindow ?: return

        // Gather minimal screen data
        val screenContext = gatherScreenContext(rootNode)

        // Make an async call to the backend orchestrator
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = OrchestratorRequest(
                    userGoal = userGoal,
                    screenContext = screenContext
                )
                val response = OrchestratorApi.service.getPlan(request)
                applySteps(service, rootNode, response.steps)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun gatherScreenContext(rootNode: AccessibilityNodeInfo): ScreenContext {
// Build a list of ScreenElement from the immediate children (example logic).
        val elements = mutableListOf<ScreenElement>()
        for (i in 0 until rootNode.childCount) {
            val child = rootNode.getChild(i) ?: continue
            val element = ScreenElement(
                elementId = child.viewIdResourceName ?: "no_id",
                text = child.text?.toString(),
                description = child.contentDescription?.toString(),
                resourceId = child.viewIdResourceName
            )
            elements.add(element)
        }
        return ScreenContext(
            appName = "UnknownApp", // In practice, glean from event/package if needed
            elements = elements
        )
    }

    private fun applySteps(
        service: MyAccessibilityService,
        rootNode: AccessibilityNodeInfo,
        steps: List<ActionStep>
    ) {
        for (step in steps) {
            when (step.action) {
                "tap" -> {
                    val targetId = step.target["resource_id"] as? String ?: continue
                    performTap(rootNode, targetId)
                }
                "fill" -> {
                    val targetId = step.target["resource_id"] as? String ?: continue
                    val text = step.target["text"] as? String ?: ""
                    performFill(rootNode, targetId, text)
                }
// Add more action types if needed
            }
        }
    }

    private fun performTap(rootNode: AccessibilityNodeInfo, resourceId: String) {
        val nodes = rootNode.findAccessibilityNodeInfosByViewId(resourceId)
        nodes?.forEach { node ->
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
    }

    private fun performFill(rootNode: AccessibilityNodeInfo, resourceId: String, text: String) {
        val nodes = rootNode.findAccessibilityNodeInfosByViewId(resourceId)
        nodes?.forEach { node ->
            val args = Bundle()
            args.putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                text
            )
            node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
        }
    }

}