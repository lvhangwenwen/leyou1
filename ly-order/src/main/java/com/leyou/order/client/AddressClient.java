package com.leyou.order.client;

import com.leyou.order.DTO.AddressDTO;

import java.util.ArrayList;
import java.util.List;

public class AddressClient {
    public static final List<AddressDTO> addressList=new ArrayList<AddressDTO>(){
        {
            AddressDTO address = new AddressDTO();
            address.setId(1L);
            address.setAddress("航头镇航头路吕航大厦3号楼");
            address.setCity("河源");
            address.setDistrict("源城区");
            address.setName("航哥");
            address.setPhone("13827830964");
            address.setZipCode("517000");
            address.setIsDefault(true);
            add(address);

            AddressDTO adress2=new AddressDTO();
            adress2.setId(2L);
            adress2.setAddress("海大路1号");
            adress2.setCity("湛江");
            adress2.setDistrict("麻章区");
            adress2.setName("吕小布");
            adress2.setPhone("17325774014");
            adress2.setState("广东省");
            adress2.setZipCode("000000");
            adress2.setIsDefault(false);
            add(adress2);
        }
    };
    public static AddressDTO findById(Long id){
        for (AddressDTO addressDTO : addressList) {
            if (addressDTO.getId() ==id)
                return addressDTO;
        }
        return null;
    }

}
