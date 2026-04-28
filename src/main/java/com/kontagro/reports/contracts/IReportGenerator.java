package com.kontagro.reports.contracts;

import java.util.List;

public interface IReportGenerator<T>{
    byte[] generateExcel(List<T> data);
    byte[] generatePdf(List<T> data);
}
