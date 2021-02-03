/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.chrisbanes.insetter.sample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * A simple [RecyclerView.Adapter] which displays a fake conversation,
 * used in [AnimatedInsetterSample].
 */
internal class ConversationAdapter : RecyclerView.Adapter<MessageHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = when (viewType) {
            ITEM_TYPE_MESSAGE_SELF -> {
                inflater.inflate(R.layout.message_bubble_self, parent, false)
            }
            else -> {
                inflater.inflate(R.layout.message_bubble_other, parent, false)
            }
        }
        return MessageHolder(view)
    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        // We don't actually do any binding
    }

    override fun getItemViewType(position: Int): Int {
        // We alternate to mimic a real conversation
        return if (position % 2 == 0) ITEM_TYPE_MESSAGE_OTHER else ITEM_TYPE_MESSAGE_SELF
    }

    override fun getItemCount(): Int = NUMBER_MESSAGES

    companion object {
        const val ITEM_TYPE_MESSAGE_SELF = 0
        const val ITEM_TYPE_MESSAGE_OTHER = 1
        const val NUMBER_MESSAGES = 50
    }
}

internal class MessageHolder(view: View) : RecyclerView.ViewHolder(view)
