/*
 * MIT License
 * 
 * Copyright (c) 2018-2019 Fabio Lima
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.f4b6a3.uuid.nodeid;

import com.github.f4b6a3.uuid.util.NodeIdentifierUtil;

public class FixedNodeIdentifierStrategy implements NodeIdentifierStrategy {

	protected long nodeIdentifier;

	protected static final long NODEID_MAX = 0x0000FFFFFFFFFFFFL;

	public FixedNodeIdentifierStrategy(long nodeIdentifier) {
		this(nodeIdentifier, false);
	}

	public FixedNodeIdentifierStrategy(long nodeIdentifier, boolean multicast) {
		this.nodeIdentifier = nodeIdentifier & NODEID_MAX;
		if (multicast) {
			this.nodeIdentifier = NodeIdentifierUtil.setMulticastNodeIdentifier(this.nodeIdentifier);
		}
	}

	@Override
	public long getNodeIdentifier() {
		return this.nodeIdentifier;
	}
}