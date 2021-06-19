package leaktest

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
  val driver = JdbcSqliteDriver("jdbc:sqlite:leakdb.sqlite") // File is checked into Git
  val db = TestDb(driver)

  // Code used to populate the db:
  //  TestDb.Schema.create(driver)
  //  repeat(500) {
  //    db.testTableQueries.insertOne(UUID.randomUUID().toString(), "Hello", (Math.random() * 1000000L).toLong())
  //  }

  val pid = ProcessHandle.current().pid()
  println("I am process $pid")

  runBlocking {
    launch {
      while (isActive) {
        val memory = getResidentMemory(pid)
        println("Memory usage: ${String.format("%.2f", memory / 1024.0)}M")
        delay(2000)
      }
    }

    launch {
      while (isActive) {
        db.testTableQueries.getLatest().executeAsOneOrNull()
        delay(100)
      }
    }
  }
}

fun getResidentMemory(pid: Long): Long {
  val memoryUsageCmd = listOf(
    "sh", "-c", """ps x -o pid,rss,vsz,command | grep '^\s*$pid'"""
  )
  val process = Runtime.getRuntime().exec(memoryUsageCmd.toTypedArray())
  val output = process.inputStream.reader().buffered().readLine()
  val parts = output.split("""\s+""".toRegex())
  return parts[1].toLong()
}
