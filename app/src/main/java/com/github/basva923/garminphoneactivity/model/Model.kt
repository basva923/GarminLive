package com.github.basva923.garminphoneactivity.model

object Model {
    var track = Track()

    val modelUpdateReceivers = mutableListOf<ModelUpdateReceiver>()

    fun modelChanged() {
        modelUpdateReceivers.forEach {
            it.onModelUpdate()
        }
    }
}