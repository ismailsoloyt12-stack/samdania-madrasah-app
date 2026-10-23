package com.example

import com.example.data.model.KvItem
import com.example.data.remote.SupabaseSqlGenerator
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun `verify kv_store SQL contains table and public RLS policy`() {
    val sql = SupabaseSqlGenerator.generateFullSchemaSql()
    assertTrue(sql.contains("CREATE TABLE IF NOT EXISTS public.kv_store"))
    assertTrue(sql.contains("key   TEXT PRIMARY KEY"))
    assertTrue(sql.contains("value TEXT NOT NULL"))
    assertTrue(sql.contains("CREATE POLICY \"kv_store_public_all\""))
  }

  @Test
  fun `verify KvItem model data properties`() {
    val item = KvItem(key = "students", value = "[]")
    assertEquals("students", item.key)
    assertEquals("[]", item.value)
  }
}
