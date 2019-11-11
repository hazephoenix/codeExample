package triton.protocol.packet

class ValuePacketDataXf(
        /**
         * Слово флагов состояния датчиков (2байта)
         *
         * DWORD ProbeFlags
         */
        val probeFlags: UInt,

        /**
         * Значение SPO2
         *
         * WORD SpO2;
         */
        val spO2: UShort,

        /**
         * Значение HR
         *
         * ЧСС/ЧП
         *
         * WORD HR;
         */
        val hr: UShort,

        /**
         * Значение EtCO2
         *
         * WORD EtCO2;
         */
        val etCO2: UShort,

        /**
         * Значение FiCO2
         *
         * WORD FiCO2;
         */
        val fiCO2: UShort,

        /**
         * Значение RSPC
         *
         * Частота дыхания по Пульсоксиметру
         *
         * WORD RSPC;
         */
        val rspc: UShort,

        /**
         * Значение RSPECG
         *
         * Частота дыхания по ЭКГ
         *
         * WORD RSPECG;
         */
        val rspEcg: UShort,

        /**
         * Значение T1
         *
         * WORD T1;
         */
        val t1: UShort,

        /**
         * Значение T2
         *
         * WORD T2;
         */
        val t2: UShort,

        /**
         * Значение NIBP_Sys
         *
         * WORD NIBP_Sys;
         */
        val nibpSys: UShort,

        /**
         * Значение NIBP_Dsys
         *
         * WORD NIBP_Dsys;
         */
        val nibpDsys: UShort,

        /**
         * Значение NIBP_Med
         *
         * WORD NIBP_Med;
         */
        val nibpMed: UShort,

        /**
         * Время цикла измерениЯ давлениЯ
         *
         * WORD NIBP_TCycle;
         */
        val nibpTCycle: UShort,

        /**
         * Время до следующего измерениЯ давления
         *
         * WORD NIBP_TNextMeasure;
         */
        val nibpTNextMeasure: UShort,

        /**
         * Наполнение PPG
         *
         * BYTE PPG_Filling;
         */
        val ppgFilling: UByte,

        /**
         * Масштаб ECG
         *
         * BYTE ECG_Scale;
         */
        val ecgScale: UByte,

        /**
         * ???
         */
        val VCO2: UShort,

        /**
         * ???
         */
        val VO2: UShort,

        /**
         * ???
         */
        val VE: UShort
)