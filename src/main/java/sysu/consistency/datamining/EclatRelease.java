package sysu.consistency.datamining;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class EclatRelease {
	private File file;
	private float limitValue=0.002f;
	private int transNum=0;
	private ArrayList<HeadNode> array=new ArrayList<HeadNode>();
	private HashHeadNode[] hashTable;//�����ʱ���ɵ�Ƶ�������Ϊ�ظ���ѯ�ı�ѡ����
	public long newItemNum=0;
	
	private File tempFile=null;
	private BufferedWriter bw=null;
	
	public static long modSum=0;

	public EclatRelease(String type){
		file = new File("D:/temp/"+type+"/data.txt");
	}
	
	/**
	 * ��һ��ɨ�����ݿ⣬ȷ��Itemset,������ֵ�����֧�ֶ���
	 */
	public void init()
	{
		Set<String> itemSet=new TreeSet<String>();
		MyMap<Integer,Integer> itemMap=new MyMap<Integer,Integer>();
		
		int itemNum=0;
		Set[][] a;
		try {
			FileInputStream fis=new FileInputStream(file);
			BufferedReader br=new BufferedReader(new InputStreamReader(fis));
			String str=null;
			
			//��һ��ɨ�����ݼ���
			while((str=br.readLine()) != null)
			{
				transNum++;
				String[] line=str.split(" ");
				for(String item:line)
				{
					itemSet.add(item);
					itemMap.add(Integer.parseInt((item)));
				}
			}
			br.close();
			ItemSet.limitSupport=(int)Math.floor(transNum*limitValue);//��ȡ��
			ItemSet.ItemSize=(Integer)itemMap.lastKey();
			ItemSet.TransSize=transNum;
			hashTable=new HashHeadNode[ItemSet.ItemSize*3];//�����hash��
			for(int i=0;i<hashTable.length;i++)
			{
				hashTable[i]=new HashHeadNode();
			}
			
			
			
			tempFile=new File(file.getParent()+"/"+file.getName()+".dat");
			if(tempFile.exists())
			{
				tempFile.delete();
			}
			tempFile.createNewFile();
			
			
			bw=new BufferedWriter(new FileWriter(tempFile));
			
			
			
			Set oneItem=itemMap.keySet();
			int countOneItem=0;
			for(Iterator it=oneItem.iterator();it.hasNext();)
			{
				int key=(Integer)it.next();
				int value=(Integer)itemMap.get(key);
				if(value >= ItemSet.limitSupport)
				{
					bw.write(key+" "+":"+" "+value);
					bw.write("\n");
					countOneItem++;
				}
			}
			bw.flush();
			modSum+=countOneItem;
			
			itemNum=(Integer)itemMap.lastKey();
			
			a=new TreeSet[itemNum+1][itemNum+1];
			array.add(new HeadNode());//����
			
			for(short i=1;i<=itemNum;i++)
			{
				HeadNode hn=new HeadNode();
				array.add(hn);
			}
			
			BufferedReader br2=new BufferedReader(new FileReader(file));
			
			//�ڶ���ɨ�����ݼ���,�γ�2-���ѡ��
			int counter=0;//����
			int max=0;
			while((str=br2.readLine()) != null)
			{max++;
				String[] line=str.split(" ");
				counter++;
				for(int i=0;i<line.length;i++)
				{
					int sOne=Integer.parseInt(line[i]);
					for(int j=i+1;j<line.length;j++)
					{
						int sTwo=Integer.parseInt(line[j]);
						if(a[sOne][sTwo] == null)
						{
							Set set=new TreeSet();
							set.add(counter);
							a[sOne][sTwo]=set;
						}
						else{
							a[sOne][sTwo].add(counter);
													
						}
					}
				}
			}
			//�����鼯��ת��Ϊ������
			
			for(int i=1;i<=itemNum;i++)
			{
				HeadNode hn=array.get(i);
				for(int j=i+1;j<=itemNum;j++)
				{
					if(a[i][j] != null && a[i][j].size() >= ItemSet.limitSupport)
					{
						hn.items++;
						ItemSet is=new ItemSet(true);
						is.item=2;
						is.items.set(i);
						is.items.set(j);
						is.supports=a[i][j].size();
						bw.write(i+" "+j+" "+": "+is.supports);
						bw.write("\n");
						//ͳ��Ƶ��2-��ĸ���
						modSum++;
						for(Iterator it=a[i][j].iterator();it.hasNext();)
						{
							int value=(Integer)it.next();
							is.trans.set(value);
						}
						if( hn.first== null)
						{
							hn.first=is;
							hn.last=is;
						}
						else{
							hn.last.next=is;
							hn.last=is;
						}
					}
				}
			}
			bw.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void start()
	{
		boolean flag=true;
		//TreeSet ts=new TreeSet();//��ʱ�洢��Ŀ���ϣ���ֹ�ظ�����֣���ʡ�ռ�
		
		int count=0;
		
		ItemSet shareFirst=new ItemSet(false);
		
		while(flag)
		{
			flag=false;
			//System.out.println(++count);
			for(int i=1;i<array.size();i++)
			{
				HeadNode hn=array.get(i);
				
				
				if(hn.items > 1 )//���������1
				{	
					generateLargeItemSet(hn,shareFirst);
					flag=true;
					
				}
				clear(hashTable);
			}
			
		}try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void generateLargeItemSet(HeadNode hn,ItemSet shareFirst){
		
		
		BitSet bsItems=new BitSet(ItemSet.ItemSize);//���������k-1Ƶ�����ItemSet��
		BitSet bsTrans=new BitSet(ItemSet.TransSize);//�������k-1Ƶ�����Trans��
		BitSet containItems=new BitSet(ItemSet.ItemSize);//�������k-1Ƶ�����ItemSet�Ĳ�
		BitSet bsItems2=new BitSet(ItemSet.ItemSize);//��ʱ�������BitSet
		
		ItemSet oldCurrent=null,oldNext=null;
		oldCurrent=hn.first;
		long countItems=0;
		
		ItemSet newFirst=new ItemSet(false),newLast=newFirst;
		while(oldCurrent != null)
		{
			oldNext=oldCurrent.next;
			while(oldNext != null)
			{
				//����k�����ѡ����������k-1��Ƶ��������
				bsItems.clear();
				bsItems.or(oldCurrent.items);
				bsItems.and(oldNext.items);
				
				if(bsItems.cardinality() < oldCurrent.item-1)
				{
					break;
				}
				//�ºϲ�����Ƿ��Ѿ�����
				
				containItems.clear();
				containItems.or(oldCurrent.items);//��k-1��ϲ�
				containItems.or(oldNext.items);
				
				if(!containItems(containItems,bsItems2,newFirst)){
					
					bsTrans.clear();
					bsTrans.or(oldCurrent.trans);
					bsTrans.and(oldNext.trans);
					if(bsTrans.cardinality() >= ItemSet.limitSupport)
					{
						ItemSet is=null;
						
						if(shareFirst.next == null)//û�й���ItemSet����
						{
							is=new ItemSet(true);
							newItemNum++;
						}
						else
						{
							is=shareFirst.next;
							shareFirst.next=shareFirst.next.next;
							
							is.items.clear();
							is.trans.clear();
							is.next=null;
							
						}
						is.item=(oldCurrent.item+1);//����k�����ѡ����������k-1��Ƶ��������
						
						is.items.or(oldCurrent.items);//��k-1��ϲ�
						is.items.or(oldNext.items);//��k-1��ϲ�
						
						is.trans.or(oldCurrent.trans);//��bs1��ֵ���Ƶ�bs��
						is.trans.and(oldNext.trans);
						
						is.supports=is.trans.cardinality();
						
						writeToFile(is.items,is.supports);//��Ƶ�������֧�ֶ�д���ļ�
						countItems++;
						
						modSum++;
						newLast.next=is;
						newLast=is;
						
					}
				}
				oldNext=oldNext.next;
			}
			oldCurrent=oldCurrent.next;
		}
		
		ItemSet temp1=hn.first;
		ItemSet temp2=hn.last;
		
		temp2.next=shareFirst.next;
		shareFirst.next=temp1;
		
		hn.first=newFirst.next;
		hn.last=newLast;
		hn.items=countItems;
		
	}
	
	public boolean containItems(BitSet containItems,BitSet bsItems2,ItemSet first)
	{
		long size=containItems.cardinality();//���Ŀ
		
		int itemSum=0;
		int temp=containItems.nextSetBit(0);
		while(true)
		{
			itemSum+=temp;
			temp=containItems.nextSetBit(temp+1);
			if(temp == -1)
			{
				break;
			}
		}
		
		int hash=itemSum%(ItemSet.ItemSize*3);
		
		HashNode hn=hashTable[hash].next;
		Node pre=hashTable[hash];
		while(true)
		{
			if(hn == null)//������containItems
			{
				HashNode node=new HashNode();
				node.bs.or(containItems);
				
				pre.next=node;
				
				return false;
			}
			if(hn.bs.isEmpty())
			{
				hn.bs.or(containItems);
				
				return false;
			}
			
			bsItems2.clear();
			bsItems2.or(containItems);
			bsItems2.and(hn.bs);
			
			if(bsItems2.cardinality() == size)
			{
				return true;
			}
			pre=hn;
			hn=hn.next;
		}
		
	}
	
	public void clear(HashHeadNode[] hashTable)
	{
		for(int i=0;i<hashTable.length;i++)
		{
			HashNode node=hashTable[i].next;
			while(node != null)
			{
				node.bs.clear();
				node=node.next;
			}
		}
	}
	
	public void writeToFile(BitSet items,int supports)
	{
		StringBuilder sb=new StringBuilder();
		int temp=items.nextSetBit(0);
		sb.append(temp);
		while(true)
		{
			temp=items.nextSetBit(temp+1);
			if(temp == -1)
			{
				break;
			}
			sb.append(" ");
			sb.append(temp);
		}
		sb.append(" :"+" "+supports);
		try {
			bw.write(sb.toString());
			bw.write("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public  void run() {
		long begin=System.currentTimeMillis();
		init();
		start();
		long end=System.currentTimeMillis();
		
		double time=(double)(end-begin)/1000;
		System.out.println("����ʱ"+time+"��");
		System.out.println("Ƶ��ģʽ��Ŀ:"+EclatRelease.modSum);
		}
	}

class MyMap<T,E> extends TreeMap
{
	public void add(T obj)
	{
		if(this.containsKey(obj))
		{
			int value=(Integer)this.get(obj);
			this.put(obj, value+1);
		}
		else 
			this.put(obj, 1);
	}
}