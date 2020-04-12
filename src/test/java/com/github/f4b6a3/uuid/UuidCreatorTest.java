package com.github.f4b6a3.uuid;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.time.Instant;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import com.github.f4b6a3.uuid.creator.AbstractTimeBasedUuidCreator;
import com.github.f4b6a3.uuid.creator.NoArgumentsUuidCreator;
import com.github.f4b6a3.uuid.creator.rfc4122.DceSecurityUuidCreator;
import com.github.f4b6a3.uuid.creator.rfc4122.NameBasedMd5UuidCreator;
import com.github.f4b6a3.uuid.creator.rfc4122.NameBasedSha1UuidCreator;
import com.github.f4b6a3.uuid.creator.rfc4122.RandomBasedUuidCreator;
import com.github.f4b6a3.uuid.creator.rfc4122.TimeBasedUuidCreator;
import com.github.f4b6a3.uuid.creator.rfc4122.TimeOrderedUuidCreator;
import com.github.f4b6a3.uuid.enums.UuidNamespace;
import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.strategy.clockseq.DefaultClockSequenceStrategy;
import com.github.f4b6a3.uuid.strategy.timestamp.FixedTimestampStretegy;
import com.github.f4b6a3.uuid.util.NodeIdentifierUtil;
import com.github.f4b6a3.uuid.util.UuidTimeUtil;
import com.github.f4b6a3.uuid.util.UuidUtil;
import static com.github.f4b6a3.commons.util.ByteUtil.*;

public class UuidCreatorTest {

	private static final String GITHUB_URL = "www.github.com";
	private static int processors;

	private static final int COUNTER_OFFSET_MAX = 256;
	private static final int DEFAULT_LOOP_MAX = 10_000 - COUNTER_OFFSET_MAX;

	private static final String UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-6][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

	private static final String DUPLICATE_UUID_MSG = "A duplicate UUID was created";
	private static final String CLOCK_SEQUENCE_MSG = "The last clock sequence should be equal to the first clock sequence minus 1";

	@BeforeClass
	public static void beforeClass() {

		processors = Runtime.getRuntime().availableProcessors();
		if (processors < 4) {
			processors = 4;
		}
	}

	@Test
	public void testTimeOrderedUuid() {
		boolean multicast = true;
		testCreateAbstractTimeBasedUuid(UuidCreator.getTimeOrderedCreator(), multicast);
	}

	@Test
	public void testTimeOrderedUuidWithMac() {
		boolean multicast = false;
		testCreateAbstractTimeBasedUuid(UuidCreator.getTimeOrderedCreator().withHardwareAddressNodeIdentifier(),
				multicast);
	}

	@Test
	public void testCreateTimeBasedUuid() {
		boolean multicast = true;
		testCreateAbstractTimeBasedUuid(UuidCreator.getTimeBasedCreator(), multicast);
	}

	@Test
	public void testCreateTimeBasedUuidWithMac() {
		boolean multicast = false;
		testCreateAbstractTimeBasedUuid(UuidCreator.getTimeBasedCreator().withHardwareAddressNodeIdentifier(),
				multicast);
	}

	@Test
	public void testRandomUuid() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		RandomBasedUuidCreator creator = UuidCreator.getRandomBasedCreator();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
		}

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkVersion(list, UuidVersion.VERSION_RANDOM_BASED.getValue());
	}

	@Test
	public void testFastRandomUuid() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		RandomBasedUuidCreator creator = UuidCreator.getRandomBasedCreator().withFastRandomGenerator();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
		}

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkVersion(list, UuidVersion.VERSION_RANDOM_BASED.getValue());
	}

	@Test
	public void testNameBasedMd5Uuid() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		NameBasedMd5UuidCreator creator = UuidCreator.getNameBasedMd5Creator();

		byte[] name;

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			name = ("url" + i).getBytes();
			list[i] = creator.create(UuidNamespace.NAMESPACE_URL.getValue(), name);
		}

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkVersion(list, UuidVersion.VERSION_NAME_BASED_MD5.getValue());

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			name = ("url" + i).getBytes();
			UUID other = creator.create(UuidNamespace.NAMESPACE_URL.getValue(), name);
			assertTrue("Two different MD5 UUIDs for the same input", list[i].equals(other));
		}
	}

	@Test
	public void testNameBasedSha1Uuid() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		NameBasedSha1UuidCreator creator = UuidCreator.getNameBasedSha1Creator();

		String name;

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			name = ("url" + i);
			list[i] = creator.create(UuidNamespace.NAMESPACE_URL, name);
		}

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkVersion(list, UuidVersion.VERSION_NAMBE_BASED_SHA1.getValue());

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			name = ("url" + i);
			UUID other = creator.create(UuidNamespace.NAMESPACE_URL, name);
			assertTrue("Two different SHA1 UUIDs for the same input", list[i].equals(other));
		}
	}

	@Test
	public void testCompGuid() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		long startTime = System.currentTimeMillis();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = UuidCreator.getCombGuid();
		}

		long endTime = System.currentTimeMillis();

		checkUniqueness(list);

		long previous = 0;
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long creationTime = list[i].getLeastSignificantBits() & 0x0000ffffffffffffL;
			assertTrue("Comb Guid creation time before start time", startTime <= creationTime);
			assertTrue("Comb Guid creation time after end time", creationTime <= endTime);
			assertTrue("Comb Guid sequence is not sorted " + previous + " " + creationTime, previous <= creationTime);
			previous = creationTime;
		}
	}

	@Test
	public void testGetTimeOrderedUuidStringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID uuid = UuidCreator.getTimeOrdered();
			checkIfStringIsValid(uuid);
		}
	}

	@Test
	public void testGetTimeOrderedWithMacStringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID uuid = UuidCreator.getTimeOrderedWithMac();
			checkIfStringIsValid(uuid);
		}
	}

	@Test
	public void testGetRandomUuidStringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID uuid = UuidCreator.getRandomBased();
			checkIfStringIsValid(uuid);
		}
	}

	@Test
	public void testGetTimeBasedUuidStringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID uuid = UuidCreator.getTimeBased();
			checkIfStringIsValid(uuid);
		}
	}

	@Test
	public void testGetTimeBasedWithMacStringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID uuid = UuidCreator.getTimeBasedWithMac();
			checkIfStringIsValid(uuid);
		}
	}

	@Test
	public void testGetTimeOrderedTimestampBitsAreTimeOrdered() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		TimeOrderedUuidCreator creator = UuidCreator.getTimeOrderedCreator();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
		}

		long oldTimestemp = 0;
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long newTimestamp = UuidUtil.extractTimestamp(list[i]);

			if (i > 0) {
				assertTrue(newTimestamp >= oldTimestemp);
			}
			oldTimestemp = newTimestamp;
		}
	}

	@Test
	public void testGetTimeOrderedMostSignificantBitsAreTimeOrdered() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		TimeOrderedUuidCreator creator = UuidCreator.getTimeOrderedCreator();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
		}

		long oldMsb = 0;

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long newMsb = list[i].getMostSignificantBits();

			if (i > 0) {
				assertTrue(newMsb >= oldMsb);
			}
			oldMsb = newMsb;
		}
	}

	@Test
	public void testGetTimeBasedTimestampIsCorrect() {

		TimeBasedUuidCreator creator = UuidCreator.getTimeBasedCreator();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			Instant instant1 = Instant.now();

			UUID uuid = creator.withTimestampStrategy(new FixedTimestampStretegy(instant1)).create();
			Instant instant2 = UuidUtil.extractInstant(uuid);

			long timestamp1 = UuidTimeUtil.toTimestamp(instant1);
			long timestamp2 = UuidTimeUtil.toTimestamp(instant2);

			assertEquals(timestamp1, timestamp2);
		}
	}

	@Test
	public void testGetTimeOrderedTimestampIsCorrect() {

		TimeOrderedUuidCreator creator = UuidCreator.getTimeOrderedCreator();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			Instant instant1 = Instant.now();

			UUID uuid = creator.withTimestampStrategy(new FixedTimestampStretegy(instant1)).create();
			Instant instant2 = UuidUtil.extractInstant(uuid);

			long timestamp1 = UuidTimeUtil.toTimestamp(instant1);
			long timestamp2 = UuidTimeUtil.toTimestamp(instant2);

			assertEquals(timestamp1, timestamp2);
		}
	}

	@Test
	public void testGetDCESecuritylLocalDomainAndLocalIdentifierAreCorrect() {

		DceSecurityUuidCreator creator = UuidCreator.getDceSecurityCreator();
		DceSecurityUuidCreator creatorWithMac = UuidCreator.getDceSecurityCreator().withHardwareAddressNodeIdentifier();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			byte localDomain = (byte) i;
			int localIdentifier = 1701 + 1;

			UUID uuid = creator.create(localDomain, localIdentifier);

			byte localDomain2 = UuidUtil.extractLocalDomain(uuid);
			int localIdentifier2 = UuidUtil.extractLocalIdentifier(uuid);

			assertEquals(localDomain, localDomain2);
			assertEquals(localIdentifier, localIdentifier2);

			// Test with hardware address too
			uuid = creatorWithMac.create(localDomain, localIdentifier);

			localDomain2 = UuidUtil.extractLocalDomain(uuid);
			localIdentifier2 = UuidUtil.extractLocalIdentifier(uuid);

			assertEquals(localDomain, localDomain2);
			assertEquals(localIdentifier, localIdentifier2);
		}
	}

	@Test
	public void testGetNameBasedMd5CompareWithJavaUtilUuidNameUuidFromBytes() {

		UuidNamespace namespace = UuidNamespace.NAMESPACE_DNS;
		String name = null;
		UUID uuid = null;

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			name = UuidCreator.getRandomBased().toString();
			uuid = UuidCreator.getNameBasedMd5(namespace, name);

			byte[] namespaceBytes = toBytes(namespace.getValue().toString().replaceAll("-", ""));
			byte[] nameBytes = name.getBytes();
			byte[] bytes = concat(namespaceBytes, nameBytes);

			assertEquals(UUID.nameUUIDFromBytes(bytes).toString(), uuid.toString());
		}
	}

	@Test
	public void testGetNameBasedMd5NamespaceDnsAndSiteGithub() {

		UuidNamespace namespace = UuidNamespace.NAMESPACE_DNS;
		String name = GITHUB_URL;

		// Value generated by UUIDGEN (util-linux)
		UUID uuid1 = UUID.fromString("2c02fba1-0794-3c12-b62b-578ec5f03908");
		UUID uuid2 = UuidCreator.getNameBasedMd5(namespace, name);
		assertEquals(uuid1, uuid2);

		// Value generated by MD5SUM (gnu-coreutils)
		UUID uuid3 = UUID.fromString("d85b3e68-c422-3cfc-b1ea-b58b6d8dfad0");
		UUID uuid4 = UuidCreator.getNameBasedMd5(name);
		assertEquals(uuid3, uuid4);

		NameBasedMd5UuidCreator creator1 = UuidCreator.getNameBasedMd5Creator().withNamespace(namespace);
		// Value generated by UUIDGEN (util-linux)
		UUID uuid5 = UUID.fromString("2c02fba1-0794-3c12-b62b-578ec5f03908");
		UUID uuid6 = creator1.create(name);
		assertEquals(uuid5, uuid6);
	}

	@Test
	public void testGetNameBasedSha1NamespaceDnsAndSiteGithub() {

		UuidNamespace namespace = UuidNamespace.NAMESPACE_DNS;
		String name = GITHUB_URL;

		// Value generated by UUIDGEN (util-linux)
		UUID uuid1 = UUID.fromString("04e16ed4-cd93-55f3-b2e3-1a097fc19832");
		UUID uuid2 = UuidCreator.getNameBasedSha1(namespace, name);
		assertEquals(uuid1, uuid2);

		// Value generated by SHA1SUM (gnu-coreutils)
		UUID uuid3 = UUID.fromString("a2999f4b-523d-5e63-866a-d0d9f401fe93");
		UUID uuid4 = UuidCreator.getNameBasedSha1(name);
		assertEquals(uuid3, uuid4);
	}

	@Test
	public void testUniquenesWithParallelThreadsMakingRequestingToASingleGenerator() {
		boolean verbose = false;
		int threadCount = 16; // Number of threads to run
		int requestCount = 100_000; // Number of requests for thread
		UniquenessTest.execute(verbose, threadCount, requestCount);
	}

	@Test
	public void testGetTimeBasedShouldCreateAlmostSixteenThousandUniqueUuidsWithTheTimeStopped() {

		int max = 0x3fff + 1; // 16,384
		Instant instant = Instant.now();
		HashSet<UUID> set = new HashSet<>();

		// Reset the static ClockSequenceController
		// It could affect this test case
		DefaultClockSequenceStrategy.CONTROLLER.clearPool();

		// Instantiate a factory with a fixed timestamp, to simulate a request
		// rate greater than 16,384 per 100-nanosecond interval.
		TimeBasedUuidCreator creator = UuidCreator.getTimeBasedCreator()
				.withTimestampStrategy(new FixedTimestampStretegy(instant));

		int firstClockSeq = 0;
		int lastClockSeq = 0;

		// Try to create 16,384 unique UUIDs
		for (int i = 0; i < max; i++) {
			UUID uuid = creator.create();
			if (i == 0) {
				firstClockSeq = UuidUtil.extractClockSequence(uuid);
			} else if (i == max - 1) {
				lastClockSeq = UuidUtil.extractClockSequence(uuid);
			}

			// Fail if the insertion into the hash set returns false, indicating
			// that there's a duplicate UUID.
			assertTrue(DUPLICATE_UUID_MSG, set.add(uuid));
		}

		assertTrue(DUPLICATE_UUID_MSG, set.size() == max);
		assertTrue(CLOCK_SEQUENCE_MSG, (lastClockSeq % max) == ((firstClockSeq % max) - 1));
	}

	@Test
	public void testGetTimeOrderedShouldCreateAlmostSixteenThousandUniqueUuidsWithTheTimeStopped() {

		int max = 0x3fff + 1; // 16,384
		Instant instant = Instant.now();
		HashSet<UUID> set = new HashSet<>();

		// Reset the static ClockSequenceController
		// It could affect this test case
		DefaultClockSequenceStrategy.CONTROLLER.clearPool();

		// Instantiate a factory with a fixed timestamp, to simulate a request
		// rate greater than 16,384 per 100-nanosecond interval.
		TimeOrderedUuidCreator creator = UuidCreator.getTimeOrderedCreator()
				.withTimestampStrategy(new FixedTimestampStretegy(instant));

		int firstClockSeq = 0;
		int lastClockSeq = 0;

		// Try to create 16,384 unique UUIDs
		for (int i = 0; i < max; i++) {
			UUID uuid = creator.create();
			if (i == 0) {
				firstClockSeq = UuidUtil.extractClockSequence(uuid);
			} else if (i == max - 1) {
				lastClockSeq = UuidUtil.extractClockSequence(uuid);
			}
			// Fail if the insertion into the hash set returns false, indicating
			// that there's a duplicate UUID.
			assertTrue(DUPLICATE_UUID_MSG, set.add(uuid));
		}

		assertTrue(DUPLICATE_UUID_MSG, set.size() == max);
		assertTrue(CLOCK_SEQUENCE_MSG, (lastClockSeq % max) == ((firstClockSeq % max) - 1));
	}

	@Test
	public void testGetTimeBasedParallelGeneratorsShouldCreateUniqueUuids() throws InterruptedException {

		Thread[] threads = new Thread[processors];
		TestThread.clearHashSet();

		// Instantiate and start many threads
		for (int i = 0; i < processors; i++) {
			threads[i] = new TestThread(UuidCreator.getTimeBasedCreator(), DEFAULT_LOOP_MAX);
			threads[i].start();
		}

		// Wait all the threads to finish
		for (Thread thread : threads) {
			thread.join();
		}

		// Check if the quantity of unique UUIDs is correct
		assertTrue(DUPLICATE_UUID_MSG, TestThread.hashSet.size() == (DEFAULT_LOOP_MAX * processors));
	}

	@Test
	public void testGetTimeOrderedParallelGeneratorsShouldCreateUniqueUuids() throws InterruptedException {

		Thread[] threads = new Thread[processors];
		TestThread.clearHashSet();

		// Instantiate and start many threads
		for (int i = 0; i < processors; i++) {
			threads[i] = new TestThread(UuidCreator.getTimeOrderedCreator(), DEFAULT_LOOP_MAX);
			threads[i].start();
		}

		// Wait all the threads to finish
		for (Thread thread : threads) {
			thread.join();
		}

		// Check if the quantity of unique UUIDs is correct
		assertTrue(DUPLICATE_UUID_MSG, TestThread.hashSet.size() == (DEFAULT_LOOP_MAX * processors));
	}

	@Test
	public void testCreateTimeBasedUuidTheGreatestDateAndTimeShouldBeAtYear5236() {

		// Check if the greatest 60 bit timestamp corresponds to the date and
		// time
		long timestamp0 = 0x0fffffffffffffffL;
		Instant instant0 = Instant.parse("5236-03-31T21:21:00.684697500Z");
		assertEquals(UuidTimeUtil.toInstant(timestamp0), instant0);

		// Test the extraction of the maximum 60 bit timestamp
		long timestamp1 = 0x0fffffffffffffffL;
		TimeBasedUuidCreator creator1 = UuidCreator.getTimeBasedCreator()
				.withTimestampStrategy(new FixedTimestampStretegy(timestamp1));
		UUID uuid1 = creator1.create();
		long timestamp2 = UuidUtil.extractTimestamp(uuid1);
		assertEquals(timestamp1, timestamp2);

		// Test the extraction of the maximum date and time
		TimeBasedUuidCreator creator2 = UuidCreator.getTimeBasedCreator()
				.withTimestampStrategy(new FixedTimestampStretegy(timestamp0));
		UUID uuid2 = creator2.create();
		Instant instant2 = UuidUtil.extractInstant(uuid2);
		assertEquals(instant0, instant2);
	}

	private void checkIfStringIsValid(UUID uuid) {
		assertTrue(uuid.toString().matches(UuidCreatorTest.UUID_PATTERN));
	}

	private void checkNullOrInvalid(UUID[] list) {
		for (UUID uuid : list) {
			assertTrue("UUID is null", uuid != null);
			assertTrue("UUID is not RFC-4122 variant", UuidUtil.isRfc4122(uuid));
		}
	}

	private void checkVersion(UUID[] list, int version) {
		for (UUID uuid : list) {
			assertTrue(String.format("UUID is not version %s", version), uuid.version() == version);
		}
	}

	private void testCreateAbstractTimeBasedUuid(AbstractTimeBasedUuidCreator creator, boolean multicast) {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
		}

		long endTime = System.currentTimeMillis();

		checkNullOrInvalid(list);
		checkVersion(list, creator.getVersion());
		checkCreationTime(list, startTime, endTime);
		checkNodeIdentifier(list, multicast);
		checkOrdering(list);
		checkUniqueness(list);

	}

	private void checkUniqueness(UUID[] list) {

		HashSet<UUID> set = new HashSet<>();

		for (UUID uuid : list) {
			assertTrue(String.format("UUID is duplicated %s", uuid), set.add(uuid));
		}

		assertTrue("There are duplicated UUIDs", set.size() == list.length);
	}

	private void checkCreationTime(UUID[] list, long startTime, long endTime) {

		assertTrue("Start time was after end time", startTime <= endTime);

		for (UUID uuid : list) {
			long creationTime = UuidUtil.extractUnixMilliseconds(uuid);
			assertTrue("Creation time was before start time " + creationTime + " " + startTime,
					creationTime >= startTime);
			assertTrue("Creation time was after end time", creationTime <= endTime);
		}

	}

	private void checkNodeIdentifier(UUID[] list, boolean multicast) {
		for (UUID uuid : list) {
			long nodeIdentifier = UuidUtil.extractNodeIdentifier(uuid);

			if (multicast) {
				assertTrue("Node identifier is not multicast",
						NodeIdentifierUtil.isMulticastNodeIdentifier(nodeIdentifier));
			}
		}
	}

	private void checkOrdering(UUID[] list) {
		UUID[] other = Arrays.copyOf(list, list.length);
		Arrays.sort(other);

		for (int i = 0; i < list.length; i++) {
			assertTrue("The UUID list is not ordered", list[i].equals(other[i]));
		}
	}

	private static class TestThread extends Thread {

		private static Set<UUID> hashSet = new HashSet<>();
		private NoArgumentsUuidCreator creator;
		private int loopLimit;

		public TestThread(NoArgumentsUuidCreator creator, int loopLimit) {
			this.creator = creator;
			this.loopLimit = loopLimit;
		}

		public static void clearHashSet() {
			hashSet = new HashSet<>();
		}

		@Override
		public void run() {
			for (int i = 0; i < loopLimit; i++) {
				synchronized (hashSet) {
					hashSet.add(creator.create());
				}
			}
		}
	}
}
