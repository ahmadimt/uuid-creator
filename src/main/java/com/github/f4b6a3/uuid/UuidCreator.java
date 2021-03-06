/*
 * MIT License
 * 
 * Copyright (c) 2018-2020 Fabio Lima
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

package com.github.f4b6a3.uuid;

import java.util.UUID;

import com.github.f4b6a3.uuid.creator.nonstandard.AltCombGuidCreator;
import com.github.f4b6a3.uuid.creator.nonstandard.CombGuidCreator;
import com.github.f4b6a3.uuid.creator.rfc4122.DceSecurityUuidCreator;
import com.github.f4b6a3.uuid.creator.rfc4122.NameBasedMd5UuidCreator;
import com.github.f4b6a3.uuid.creator.rfc4122.NameBasedSha1UuidCreator;
import com.github.f4b6a3.uuid.creator.rfc4122.RandomBasedUuidCreator;
import com.github.f4b6a3.uuid.creator.rfc4122.TimeBasedUuidCreator;
import com.github.f4b6a3.uuid.creator.rfc4122.TimeOrderedUuidCreator;
import com.github.f4b6a3.uuid.enums.UuidLocalDomain;
import com.github.f4b6a3.uuid.enums.UuidNamespace;

/**
 * Facade to the UUID factories.
 */
public class UuidCreator {

	private UuidCreator() {
	}

	/**
	 * Returns a Nil UUID.
	 * 
	 * The nil UUID is special UUID that has all 128 bits set to zero.
	 * 
	 * @return a Nil UUID
	 */
	public static UUID getNil() {
		return new UUID(0L, 0L);
	}

	/**
	 * Returns a random UUID.
	 * 
	 * <pre>
	 * Details: 
	 * - Version number: 4
	 * - Random generator: {@link java.security.SecureRandom}
	 * </pre>
	 * 
	 * @return a version 4 UUID
	 */
	public static UUID getRandomBased() {
		return RandomCreatorHolder.INSTANCE.create();
	}

	/**
	 * Returns a random UUID generated by a fast random generator.
	 * 
	 * <pre>
	 * Details: 
	 * - Version number: 4 
	 * - Random generator: {@link Xorshift128PlusRandom}
	 * </pre>
	 * 
	 * @return a version 4 UUID
	 */
	public static UUID getFastRandomBased() {
		return FastRandomCreatorHolder.INSTANCE.create();
	}

	/**
	 * Returns a time-based UUID.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 1
	 * - Has timestamp?: YES
	 * - Has hardware address (MAC)?: NO (random)
	 * </pre>
	 * 
	 * @return a version 1 UUID
	 */
	public static UUID getTimeBased() {
		return TimeBasedCreatorHolder.INSTANCE.create();
	}

	/**
	 * Returns a time-based UUID with hardware address.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 1 
	 * - Has timestamp?: YES 
	 * - Has hardware address (MAC)?: YES
	 * </pre>
	 * 
	 * @return a version 1 UUID
	 */
	public static UUID getTimeBasedWithMac() {
		return TimeBasedWithMacCreatorHolder.INSTANCE.create();
	}

	/**
	 * Returns a time-ordered UUID.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 6
	 * - Has timestamp?: YES 
	 * - Has hardware address (MAC)?: NO (random)
	 * </pre>
	 * 
	 * @return a version 6 UUID
	 */
	public static UUID getTimeOrdered() {
		return TimeOrderedCreatorHolder.INSTANCE.create();
	}

	/**
	 * Returns a time-ordered UUID with hardware address.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 6
	 * - Has timestamp?: YES 
	 * - Has hardware address (MAC)?: YES
	 * </pre>
	 * 
	 * @return a version 6 UUID
	 */
	public static UUID getTimeOrderedWithMac() {
		return TimeOrderedWithMacCreatorHolder.INSTANCE.create();
	}

	/**
	 * Returns a name-based UUID (MD5).
	 * 
	 * <pre>
	 * Details: 
	 * - Version number: 3 
	 * - Hash Algorithm: MD5 
	 * - Name Space: none
	 * </pre>
	 * 
	 * @param name a name string
	 * @return a version 3 UUID
	 */
	public static UUID getNameBasedMd5(String name) {
		return NameBasedMd5CreatorHolder.INSTANCE.create(name);
	}

	/**
	 * Returns a name-based UUID (MD5).
	 * 
	 * <pre>
	 * Details: 
	 * - Version number: 3 
	 * - Hash Algorithm: MD5 
	 * - Name Space: none
	 * </pre>
	 * 
	 * @param name a byte array
	 * @return a version 3 UUID
	 */
	public static UUID getNameBasedMd5(byte[] name) {
		return NameBasedMd5CreatorHolder.INSTANCE.create(name);
	}

	/**
	 * Returns a name-based UUID (MD5).
	 *
	 * See: {@link UuidNamespace}.
	 * 
	 * <pre>
	 * Details: 
	 * - Version number: 3 
	 * - Hash Algorithm: MD5 
	 * - Name Space: informed by user
	 * </pre>
	 * 
	 * @param namespace a name space enumeration
	 * @param name      a name string
	 * @return a version 3 UUID
	 */
	public static UUID getNameBasedMd5(UuidNamespace namespace, String name) {
		return NameBasedMd5CreatorHolder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a name-based UUID (MD5).
	 *
	 * See: {@link UuidNamespace}.
	 * 
	 * <pre>
	 * Details: 
	 * - Version number: 3 
	 * - Hash Algorithm: MD5 
	 * - Name Space: informed by user
	 * </pre>
	 * 
	 * @param namespace a name space enumeration
	 * @param name      a byte array
	 * @return a version 3 UUID
	 */
	public static UUID getNameBasedMd5(UuidNamespace namespace, byte[] name) {
		return NameBasedMd5CreatorHolder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a name-based UUID (SHA1).
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 5 
	 * - Hash Algorithm: SHA1 
	 * - Name Space: none
	 * </pre>
	 * 
	 * @param name a name string
	 * @return a version 5 UUID
	 */
	public static UUID getNameBasedSha1(String name) {
		return NameBasedSha1CreatorHolder.INSTANCE.create(name);
	}

	/**
	 * Returns a name-based UUID (SHA1).
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 5 
	 * - Hash Algorithm: SHA1 
	 * - Name Space: none
	 * </pre>
	 * 
	 * @param name a byte array
	 * @return a version 5 UUID
	 */
	public static UUID getNameBasedSha1(byte[] name) {
		return NameBasedSha1CreatorHolder.INSTANCE.create(name);
	}

	/**
	 * Returns a name-based UUID (SHA1).
	 *
	 * See: {@link UuidNamespace}.
	 * 
	 * <pre>
	 * Details: 
	 * - Version number: 5 
	 * - Hash Algorithm: SHA1 
	 * - Name Space: informed by user
	 * </pre>
	 * 
	 * @param namespace a name space enumeration
	 * @param name      a name string
	 * @return a version 5 UUID
	 */
	public static UUID getNameBasedSha1(UuidNamespace namespace, String name) {
		return NameBasedSha1CreatorHolder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a name-based UUID (SHA1).
	 *
	 * See: {@link UuidNamespace}.
	 * 
	 * <pre>
	 * Details: 
	 * - Version number: 5 
	 * - Hash Algorithm: SHA1 
	 * - Name Space: informed by user
	 * </pre>
	 * 
	 * @param namespace a name space enumeration
	 * @param name      a byte array
	 * @return a version 5 UUID
	 */
	public static UUID getNameBasedSha1(UuidNamespace namespace, byte[] name) {
		return NameBasedSha1CreatorHolder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a DCE Security UUID.
	 *
	 * See: {@link UuidLocalDomain}.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 2 
	 * - Has hardware address (MAC)?: NO (random)
	 * </pre>
	 * 
	 * @param localDomain     a local domain enumeration
	 * @param localIdentifier a local identifier
	 * @return a version 2 UUID
	 */
	public static UUID getDceSecurity(UuidLocalDomain localDomain, int localIdentifier) {
		return DceSecurityCreatorHolder.INSTANCE.create(localDomain, localIdentifier);
	}

	/**
	 * Returns a DCE Security UUID.
	 *
	 * See: {@link UuidLocalDomain}.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 2 
	 * - Has hardware address (MAC)?: YES
	 * </pre>
	 * 
	 * @param localDomain     a local domain enumeration
	 * @param localIdentifier a local identifier
	 * @return a version 2 UUID
	 */
	public static UUID getDceSecurityWithMac(UuidLocalDomain localDomain, int localIdentifier) {
		return DceSecurityWithMacCreatorHolder.INSTANCE.create(localDomain, localIdentifier);
	}

	/**
	 * Returns a COMB GUID.
	 * 
	 * The time a SUFFIX is at the LEAST significant bits.
	 * 
	 * @return a GUID
	 */
	public static UUID getCombGuid() {
		return CombCreatorHolder.INSTANCE.create();
	}

	/**
	 * Returns an alternate COMB GUID.
	 * 
	 * The time a PREFIX is at the MOST significant bits.
	 * 
	 * @return a GUID
	 */
	public static UUID getAltCombGuid() {
		return AltCombCreatorHolder.INSTANCE.create();
	}

	/*
	 * Public static methods for creating FACTORIES of UUIDs
	 */

	/**
	 * Returns a {@link TimeBasedUuidCreator} that creates UUID version 1.
	 * 
	 * @return {@link TimeBasedUuidCreator}
	 */
	public static TimeBasedUuidCreator getTimeBasedCreator() {
		return new TimeBasedUuidCreator();
	}

	/**
	 * Returns a {@link DceSecurityUuidCreator} that creates UUID version 2.
	 * 
	 * @return {@link DceSecurityUuidCreator}
	 */
	public static DceSecurityUuidCreator getDceSecurityCreator() {
		return new DceSecurityUuidCreator();
	}

	/**
	 * Returns a {@link NameBasedMd5UuidCreator} that creates UUID version 3.
	 * 
	 * @return {@link NameBasedMd5UuidCreator}
	 */
	public static NameBasedMd5UuidCreator getNameBasedMd5Creator() {
		return new NameBasedMd5UuidCreator();
	}

	/**
	 * Returns a {@link RandomBasedUuidCreator} that creates UUID version 4.
	 * 
	 * @return {@link RandomBasedUuidCreator}
	 */
	public static RandomBasedUuidCreator getRandomBasedCreator() {
		return new RandomBasedUuidCreator();
	}

	/**
	 * Returns a {@link NameBasedSha1UuidCreator} that creates UUID version 5.
	 * 
	 * @return {@link NameBasedSha1UuidCreator}
	 */
	public static NameBasedSha1UuidCreator getNameBasedSha1Creator() {
		return new NameBasedSha1UuidCreator();
	}

	/**
	 * Returns a {@link TimeOrderedUuidCreator} that creates UUID version 6.
	 * 
	 * @return {@link TimeOrderedUuidCreator}
	 */
	public static TimeOrderedUuidCreator getTimeOrderedCreator() {
		return new TimeOrderedUuidCreator();
	}

	/**
	 * Returns a {@link CombGuidCreator}.
	 * 
	 * @return {@link CombGuidCreator}
	 */
	public static CombGuidCreator getCombCreator() {
		return new CombGuidCreator();
	}

	/**
	 * Returns a {@link AltCombGuidCreator}.
	 * 
	 * @return {@link AltCombGuidCreator}
	 */
	public static AltCombGuidCreator getAltCombCreator() {
		return new AltCombGuidCreator();
	}

	/*
	 * Private classes for lazy holders
	 */

	private static class RandomCreatorHolder {
		static final RandomBasedUuidCreator INSTANCE = getRandomBasedCreator();
	}

	private static class FastRandomCreatorHolder {
		static final RandomBasedUuidCreator INSTANCE = getRandomBasedCreator().withFastRandomGenerator();
	}

	private static class TimeOrderedCreatorHolder {
		static final TimeOrderedUuidCreator INSTANCE = getTimeOrderedCreator();
	}

	private static class TimeOrderedWithMacCreatorHolder {
		static final TimeOrderedUuidCreator INSTANCE = getTimeOrderedCreator().withMacNodeIdentifier();
	}

	private static class TimeBasedCreatorHolder {
		static final TimeBasedUuidCreator INSTANCE = getTimeBasedCreator();
	}

	private static class TimeBasedWithMacCreatorHolder {
		static final TimeBasedUuidCreator INSTANCE = getTimeBasedCreator().withMacNodeIdentifier();
	}

	private static class NameBasedMd5CreatorHolder {
		static final NameBasedMd5UuidCreator INSTANCE = getNameBasedMd5Creator();
	}

	private static class NameBasedSha1CreatorHolder {
		static final NameBasedSha1UuidCreator INSTANCE = getNameBasedSha1Creator();
	}

	private static class DceSecurityCreatorHolder {
		static final DceSecurityUuidCreator INSTANCE = getDceSecurityCreator();
	}

	private static class DceSecurityWithMacCreatorHolder {
		static final DceSecurityUuidCreator INSTANCE = getDceSecurityCreator().withMacNodeIdentifier();
	}

	private static class CombCreatorHolder {
		static final CombGuidCreator INSTANCE = getCombCreator();
	}

	private static class AltCombCreatorHolder {
		static final AltCombGuidCreator INSTANCE = getAltCombCreator();
	}
}
