// Lab 2 tester

package dinocpu.test.grader

import dinocpu._
import dinocpu.test._

import com.gradescope.jh61b.grader.{GradedTest,GradedTestRunnerJSON}
import org.junit.Test
import org.scalatest.junit.JUnitSuite
import org.junit.runner.RunWith


@RunWith(classOf[GradedTestRunnerJSON])
class Lab2Grader extends JUnitSuite {

  val maxInt = BigInt("FFFFFFFF", 16)

  def twoscomp(v: BigInt) : BigInt = {
    if (v < 0) {
      return maxInt + v + 1
    } else {
      return v
    }
  }

  @Test
  @GradedTest(name="R-type instructions", max_score=10)
  def verifyRtype() {
    // Capture all of the console output from the test
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {

      implicit val conf = new CPUConfig()

      var success = true
      for (test <- InstTests.rtype ++ InstTests.rtypeMExtension ++ InstTests.rtypeMultiCycle) {
        success = CPUTesterDriver(test, "single-cycle") && success
        if (!success) {
          println("Errored on test " + test.binary)
        }
      }

      // Dump the output of the driver above onto the system out so that the
      // gradescope function will catch it.
      System.out.print(stream)
      if (!success) fail("Test failed!")
    }
  }

  @Test
  @GradedTest(name="I-type instructions", max_score=10)
  def verifyItype() {
    // Capture all of the console output from the test
    val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {

      implicit val conf = new CPUConfig()

      var success = true
      for (test <- InstTests.itype ++ InstTests.itypeMultiCycle) {
        success = CPUTesterDriver(test, "single-cycle") && success
        if (!success) {
          println("Errored on test " + test.binary)
        }
      }

      // Dump the output of the driver above onto the system out so that the
      // gradescope function will catch it.
      System.out.print(stream)
      if (!success) fail("Test failed!")
    }
  }



  @Test
  @GradedTest(name="Load instructions", max_score=10)
  def verifyLoads() {
    // Capture all of the console output from the test
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {

      implicit val conf = new CPUConfig()

      var success = true
      for (test <- InstTests.memory.filter(_.binary.startsWith("l"))) {
        success = CPUTesterDriver(test, "single-cycle") && success
        if (!success) {
          println("Errored on test " + test.binary)
        }
      }

      // Dump the output of the driver above onto the system out so that the
      // gradescope function will catch it.
      System.out.print(stream)
      if (!success) fail("Test failed!")
    }
  }

  @Test
  @GradedTest(name="U-type instructions", max_score=10)
  def verifyUtype() {
    // Capture all of the console output from the test
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {

      implicit val conf = new CPUConfig()

      var success = true
      for (test <- InstTests.utype ++ InstTests.utypeMultiCycle) {
        success = CPUTesterDriver(test, "single-cycle") && success
        if (!success) {
          println("Errored on test " + test.binary)
        }
      }

      // Dump the output of the driver above onto the system out so that the
      // gradescope function will catch it.
      System.out.print(stream)
      if (!success) fail("Test failed!")
    }
  }




  @Test
  @GradedTest(name="store instructions", max_score=10)
  def verifyStore() {
    // Capture all of the console output from the test
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {

      implicit val conf = new CPUConfig()

      var success = true
      for (test <- InstTests.memory.filter(_.binary.startsWith("s"))) {
        success = CPUTesterDriver(test, "single-cycle") && success
        if (!success) {
          println("Errored on test " + test.binary)
        }
      }

      // Dump the output of the driver above onto the system out so that the
      // gradescope function will catch it.
      System.out.print(stream)
      if (!success) fail("Test failed!")
    }
  }

  @Test
  @GradedTest(name="All mem instructions", max_score=10)
  def verifyMemInsts() {
    // Capture all of the console output from the test
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {

      implicit val conf = new CPUConfig()

      var success = true
      for (test <- InstTests.memory ++ InstTests.memoryMultiCycle) {
        success = CPUTesterDriver(test, "single-cycle") && success
        if (!success) {
          println("Errored on test " + test.binary)
        }
      }

      // Dump the output of the driver above onto the system out so that the
      // gradescope function will catch it.
      System.out.print(stream)
      if (!success) fail("Test failed!")
    }
  }

  @Test
  @GradedTest(name="Branch instructions", max_score=10)
  def verifyBranches() {
    // Capture all of the console output from the test
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {

      implicit val conf = new CPUConfig()

      var success = true
      for (test <- InstTests.branch ++ InstTests.branchMultiCycle) {
        success = CPUTesterDriver(test, "single-cycle") && success
        if (!success) {
          println("Errored on test " + test.binary)
        }
      }

      // Dump the output of the driver above onto the system out so that the
      // gradescope function will catch it.
      System.out.print(stream)
      if (!success) fail("Test failed!")
    }
  }



  @Test
  @GradedTest(name="Jump and link instruction", max_score=10)
  def verifyJal() {
    // Capture all of the console output from the test
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {

      val tests = List[CPUTestCase](
        InstTests.nameMap("jal")
      )

      implicit val conf = new CPUConfig()

      var success = true
      for (test <- tests) {
        success = CPUTesterDriver(test, "single-cycle") && success
        if (!success) {
          println("Errored on test " + test.binary)
        }
      }

      // Dump the output of the driver above onto the system out so that the
      // gradescope function will catch it.
      System.out.print(stream)
      if (!success) fail("Test failed!")
    }
  }




  @Test
  @GradedTest(name="Jump and link register instruction", max_score=10)
  def verifyJalr() {
    // Capture all of the console output from the test
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {

      val tests = List[CPUTestCase](
        InstTests.nameMap("jalr0"), InstTests.nameMap("jalr1")
      )

      implicit val conf = new CPUConfig()

      var success = true
      for (test <- tests) {
        success = CPUTesterDriver(test, "single-cycle") && success
        if (!success) {
          println("Errored on test " + test.binary)
        }
      }

      // Dump the output of the driver above onto the system out so that the
      // gradescope function will catch it.
      System.out.print(stream)
      if (!success) fail("Test failed!")
    }
  }





  @Test
  @GradedTest(name="Full applications", max_score=10)
  def verifyApps() {
    // Capture all of the console output from the test
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {

      implicit val conf = new CPUConfig()

      var success = true
      for (test <- InstTests.smallApplications) {
        success = CPUTesterDriver(test, "single-cycle") && success
        if (!success) {
          println("Errored on test " + test.binary)
        }
      }

      // Dump the output of the driver above onto the system out so that the
      // gradescope function will catch it.
      System.out.print(stream)
      if (!success) fail("Test failed!")
    }
  }

}
