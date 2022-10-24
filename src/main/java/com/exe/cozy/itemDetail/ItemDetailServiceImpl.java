package com.exe.cozy.itemDetail;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.exe.cozy.domain.ItemDetailDto;
import com.exe.cozy.mapper.ItemMapper;
import com.exe.cozy.util.FileUtil;

@Service("itemDetailService")
public class ItemDetailServiceImpl implements ItemDetailService {
	@Autowired
	private ItemMapper itemMapper;

	@Override
	public int itemMaxNum() throws Exception {
		return itemMapper.itemMaxNum();
	}

	@Override
	public int fileMaxNum() throws Exception {
		return itemMapper.fileMaxNum();
	}
	
	@Override
	public void insertItem(ItemDetailDto idto) throws Exception {
		itemMapper.insertItem(idto);
	}

	@Override
	public void insertItem(Map<String, Object> idto) throws Exception {
		// TODO Auto-generated method stub
		itemMapper.insertItemMap(idto);
	}

	@Override
	public int getItemDataCount(String searchKey, String searchValue) throws Exception {
		return itemMapper.getItemDataCount(searchKey, searchValue);
	}

	@Override
	public void deleteItemData(int itemNum) throws Exception {
		itemMapper.deleteItemData(itemNum);

	}

	@Override
	public void updateItemHitCount(int itemNum) throws Exception {
		itemMapper.updateItemHitCount(itemNum);
	}

	@Override
	public ItemDetailDto getReadItemData(int itemNum) throws Exception {
		return itemMapper.getReadItemData(itemNum);
	}
	

	private String filePath(MultipartFile multipartFile) throws Exception {
		if (multipartFile.isEmpty()) {
			return null;
		}
		FileOutputStream fos = null;
		String fileName = multipartFile.getOriginalFilename();
		String fileExtension = fileName.substring(fileName.lastIndexOf((".")));
		String storedFileName = UUID.randomUUID().toString().replaceAll("-", "") + fileExtension;
		String filePath = "C:\\test";

		File file = new File(filePath + storedFileName);

		multipartFile.transferTo(file);

//		try {
//			byte fileData[] = multipartFile.getBytes();
//			fos = new FileOutputStream(filePath + fileName);
//
//			fos.write(fileData);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		} finally {
//			if (fos != null) {
//				fos.close();
//			}
//		}

		return storedFileName;
	}

	@Override
	public int fileWrite(MultipartFile file) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		int result = 0;
		String storedFileName = "";
		String originFileNmae = file.getOriginalFilename();
		String fileExtension = StringUtils.substringAfter(originFileNmae, ".");
		String filePath = "Z:\\Documents\\GitHub\\CozyHouse\\src\\main\\resources\\static\\assets\\img\\";

		// 1.fileWriter 호출
		storedFileName = FileUtil.FileWriter(file);

		// 2.PK키 취득
		result = itemMapper.fileMaxNum() + 1;

		// 3.DB 저장 (아이템)
		map.put("no", result);
		map.put("original_file_name", originFileNmae);
		map.put("uuid_file_name", storedFileName);
		map.put("file_path", filePath);
		map.put("file_extension", fileExtension);

		itemMapper.insertFile(map);

		return result;
	}

	

	
}
