package sysu.consistency.datamining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;  
  
public class Apriori 
{  
    private final static int SUPPORT = 1000; // ֧�ֶ���ֵ  
    private final static double CONFIDENCE = 0.7; // ���Ŷ���ֵ  
    private final static String ITEM_SPLIT = ";"; // ��֮��ķָ���  
    private final static String CON = "->"; // ��֮��ķָ���  
      
    /** 
     * �㷨������ 
     * @param dataList 
     * @return 
     */  
    public Map<String, Integer> apriori(ArrayList<String> dataList)  
    {  
        Map<String, Integer> stepFrequentSetMap = new HashMap<String, Integer>();  
        stepFrequentSetMap.putAll(findFrequentOneSets(dataList));  
          
        Map<String, Integer> frequentSetMap = new HashMap<String, Integer>();//Ƶ���  
        frequentSetMap.putAll(stepFrequentSetMap);  
          
        while(stepFrequentSetMap!=null && stepFrequentSetMap.size()>0)  
        {  
            Map<String, Integer> candidateSetMap = aprioriGen(stepFrequentSetMap);  
              
            Set<String> candidateKeySet = candidateSetMap.keySet();  
              
            //ɨ��D�����м���  
            for(String data:dataList)  
            {  
                for(String candidate:candidateKeySet)  
                {  
                    boolean flag = true;  
                    String[] strings = candidate.split(ITEM_SPLIT);  
                    for(String string:strings)  
                    {  
                        if(data.indexOf(string+ITEM_SPLIT)==-1)  
                        {  
                            flag = false;  
                            break;  
                        }  
                    }  
                    if(flag)  
                        candidateSetMap.put(candidate, candidateSetMap.get(candidate)+1);  
                }  
            }  
              
            //�Ӻ�ѡ�����ҵ�����֧�ֶȵ�Ƶ���  
            stepFrequentSetMap.clear();  
            for(String candidate:candidateKeySet)  
            {  
                Integer count = candidateSetMap.get(candidate);  
                if(count>=SUPPORT)  
                    stepFrequentSetMap.put(candidate, count);  
            }  
      
            // �ϲ�����Ƶ����  
            frequentSetMap.putAll(stepFrequentSetMap);  
        }  
          
        return frequentSetMap;  
    }  
      
    /** 
     * find frequent 1 itemsets 
     * @param dataList 
     * @return 
     */  
    private Map<String, Integer> findFrequentOneSets(ArrayList<String> dataList)  
    {  
        Map<String, Integer> resultSetMap = new HashMap<String, Integer>();  
          
        for(String data:dataList)  
        {  
            String[] strings = data.split(ITEM_SPLIT);  
            for(String string:strings)  
            {  
                string += ITEM_SPLIT;  
                if(resultSetMap.get(string)==null)  
                {  
                    resultSetMap.put(string, 1);  
                }  
                else {  
                    resultSetMap.put(string, resultSetMap.get(string)+1);  
                }  
            }  
        }  
        return resultSetMap;  
    }  
      
    /** 
     * ������һ����Ƶ����ļ���ѡ����ѡ�� 
     * @param setMap 
     * @return 
     */  
    private Map<String, Integer> aprioriGen(Map<String, Integer> setMap)  
    {  
        Map<String, Integer> candidateSetMap = new HashMap<String, Integer>();  
          
        Set<String> candidateSet = setMap.keySet();  
        for(String s1:candidateSet)  
        {  
            String[] strings1 = s1.split(ITEM_SPLIT);  
            String s1String = "";  
            for(String temp:strings1)  
                s1String += temp+ITEM_SPLIT;  
              
            for(String s2:candidateSet)  
            {  
                String[] strings2 = s2.split(ITEM_SPLIT);  
                  
                  
                boolean flag = true;  
                for(int i=0;i<strings1.length-1;i++)  
                {  
                    if(strings1[i].compareTo(strings2[i])!=0)  
                    {  
                        flag = false;  
                        break;  
                    }  
                }  
                if(flag && strings1[strings1.length-1].compareTo(strings2[strings1.length-1])<0)  
                {  
                    //���Ӳ���������ѡ  
                    String c = s1String+strings2[strings2.length-1]+ITEM_SPLIT;  
                    if(hasInfrequentSubset(c, setMap))  
                    {  
                        //��֦����ɾ����Ƶ���ĺ�ѡ  
                    }  
                    else {  
                        candidateSetMap.put(c, 0);  
                    }  
                }  
            }  
        }  
          
        return candidateSetMap;  
    }  
      
    /** 
     * ʹ������֪ʶ���жϺ�ѡ���Ƿ���Ƶ��� 
     * @param candidate 
     * @param setMap 
     * @return 
     */  
    private boolean hasInfrequentSubset(String candidateSet, Map<String, Integer> setMap)  
    {  
        String[] strings = candidateSet.split(ITEM_SPLIT);  
          
        //�ҳ���ѡ�����е��Ӽ������ж�ÿ���Ӽ��Ƿ�����Ƶ���Ӽ�  
        for(int i=0;i<strings.length;i++)  
        {  
            String subString = "";  
            for(int j=0;j<strings.length;j++)  
            {  
                if(j!=i)  
                {  
                    subString += strings[j]+ITEM_SPLIT;  
                }  
            }  
              
            if(setMap.get(subString)==null)  
                return true;  
        }  
          
        return false;  
    }  
      
    /** 
     * ��Ƶ��������������� 
     * @param frequentSetMap 
     * @return 
     */  
    public Map<String, Double> getRelationRules(Map<String, Integer> frequentSetMap)  
    {  
        Map<String, Double> relationsMap = new HashMap<String, Double>();  
        Set<String> keySet = frequentSetMap.keySet();  
  
        for(String key:keySet)  
        {  
            List<String> keySubset = subset(key);  
            for(String keySubsetItem:keySubset)  
            {  
                //�Ӽ�keySubsetItemҲ��Ƶ����  
                Integer count = frequentSetMap.get(keySubsetItem);  
                if(count!=null)  
                {  
                    Double confidence = (1.0*frequentSetMap.get(key))/(1.0*frequentSetMap.get(keySubsetItem));  
                    if(confidence>CONFIDENCE)  
                        relationsMap.put(keySubsetItem+CON+expect(key, keySubsetItem), confidence);  
                }  
            }  
        }  
          
        return relationsMap;  
    }  
      
    /** 
     * ��һ���������еķǿ����Ӽ� 
     *  
     * @param sourceSet 
     * @return 
     * Ϊ���Ժ�������������ط����������ǲ����õݹ�ķ��� 
     *  
     * �ο���http://blog.163.com/xiaohui_1123@126/blog/static/3980524020109784356915/ 
     * ˼·�����輯��S��A,B,C,D�������СΪ4��ӵ��2��4�η����Ӽ�����0-15�������Ʊ�ʾΪ0000��0001��...��1111�� 
     * ��Ӧ���Ӽ�Ϊ�ռ���{D}��...��{A,B,C,D}�� 
     */  
    private List<String> subset(String sourceSet)  
    {  
        List<String> result = new ArrayList<String>();  
          
        String[] strings = sourceSet.split(ITEM_SPLIT);  
        //�ǿ����Ӽ�  
        for(int i=1;i<(int)(Math.pow(2, strings.length))-1;i++)  
        {  
            String item = "";  
            String flag = "";  
            int ii=i;  
            do  
            {  
                flag += ""+ii%2;  
                ii = ii/2;  
            } while (ii>0);  
            for(int j=flag.length()-1;j>=0;j--)  
            {  
                if(flag.charAt(j)=='1')  
                {  
                    item = strings[j]+ITEM_SPLIT+item;  
                }  
            }  
            result.add(item);  
        }  
          
        return result;  
    }  
      
    /** 
     * �������㣬A/B 
     * @param A 
     * @param B 
     * @return 
     */  
    private String expect(String stringA,String stringB)  
    {  
        String result = "";  
          
        String[] stringAs = stringA.split(ITEM_SPLIT);  
        String[] stringBs = stringB.split(ITEM_SPLIT);  
          
        for(int i=0;i<stringAs.length;i++)  
        {  
            boolean flag = true;  
            for(int j=0;j<stringBs.length;j++)  
            {  
                if(stringAs[i].compareTo(stringBs[j])==0)  
                {  
                    flag = false;  
                    break;  
                }  
            }  
            if(flag)  
                result += stringAs[i]+ITEM_SPLIT;  
        }  
          
        return result;  
    }  
}