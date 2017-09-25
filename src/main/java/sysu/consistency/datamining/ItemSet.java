package sysu.consistency.datamining;

import java.util.BitSet;

public class ItemSet {
	public static  int limitSupport;//������ֵ���������С֧�ֶ���
	public static int ItemSize;//Items��Ŀ
	public static int TransSize; //������Ŀ
	
	public boolean flag=true; //true����ʾ��Ϊ������ItemSet,falseֻ��Ϊ��ǽڵ㣬ֻ��HashTabel��ʹ��
	
	public int item=0;// ĳ�
	
	public int supports=0;//���֧�ֶ�
	
	public BitSet items=null;
	public BitSet trans=null;
	
	//public TreeSet items=new TreeSet();//�
	//public TreeSet trans=new TreeSet();//���񼯺�
	public ItemSet next=null;//��һ���
	
	public ItemSet(boolean flag)
	{
		this.flag=flag;
		if(flag)
		{
			item=0;// ĳ�
			
			supports=0;//���֧�ֶ�
			
			items=new BitSet(ItemSize+1);
			trans=new BitSet(TransSize+1);
		}
	}
}
