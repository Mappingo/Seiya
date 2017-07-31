package com.domeastudio.mappingo.server.rest;

import com.domeastudio.mappingo.server.pojo.DemoEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class DataImport {
    /**
     * 添加模拟数据
     * @return
     */
    @RequestMapping("/data/import")
    public Boolean dataImport(){
        List<DemoEntity> demoEntityList=new ArrayList<DemoEntity>();
        for(int i=0;i<10000;i++) {
            DemoEntity demoEntity=new DemoEntity();
            demoEntityList.add(demoEntity);
        }
        return false;
    }

    /**
     * 模拟MR数据
     */
    private void addMRData(){}
    /**
     * 模拟路测数据
     */
    private void addRoadTestData(){}
    /**
     * 模拟小区数据
     */
    private void addCellData(){}
    /**
     * 模拟基站数据
     */
    private void addNodeBData(){}
}
