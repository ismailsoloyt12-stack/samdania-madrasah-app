package com.example.data.remote

object SupabaseSqlGenerator {

    fun generateFullSchemaSql(): String {
        return """
-- =====================================================================
-- SAMDANIA TALIMUL ISLAM MADRASAH, GALACHIPA
-- Supabase SQL Editor -> Run
-- Compatible with Madrasah Management Software (kv_store Table)
-- =====================================================================

-- 1. Create the Key-Value Store table used by the Madrasah Management Software
CREATE TABLE IF NOT EXISTS public.kv_store (
  key   TEXT PRIMARY KEY,
  value TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS kv_store_key_idx ON public.kv_store (key);

ALTER TABLE public.kv_store ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "kv_store_public_all" ON public.kv_store;
CREATE POLICY "kv_store_public_all"
  ON public.kv_store
  FOR ALL
  TO anon, authenticated
  USING (true)
  WITH CHECK (true);

-- Optional: Initial empty keys so the software can directly update them:
INSERT INTO public.kv_store (key, value)
VALUES 
  ('students', '[]'),
  ('attendance', '[]'),
  ('grades', '[]'),
  ('routines', '[]'),
  ('homework', '[]'),
  ('notices', '[]'),
  ('fees', '[]'),
  ('messages', '[]'),
  ('quizzes', '[]'),
  ('institution', '{"name":"সামদানিয়া তা''লীমুল ইসলাম মাদ্রাসা, গলাচিপা","address":"পীর বদরুল ইসলাম সামদানী কমপ্লেক্স, খানকায়ে ফুরফুরা শরীফ, ২ নং ওয়ার্ড, গলাচিপা, পটুয়াখালী","phone":"01792776433","established":"2006"}')
ON CONFLICT (key) DO NOTHING;
"""
    }
}
