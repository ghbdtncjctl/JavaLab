package obba;
import java.lang.reflect.*;
import java.util.*;
public class privet {
	
	public static void main(String[] args)
	{
		Scanner obj=new Scanner(System.in);
		Food[] breakfast = new Food[args.length];
		for(int i=0;i<args.length;i++){
			String ard=args[i];
			if(ard.startsWith("-"))
			{
				if(ard.compareTo("-calories")==0)
				{
				int a=0;
				for(int j=0;j<i;j++)
				{
					if(breakfast[j]!=null)
					a+=breakfast[j].calculatecalories();
				}
				System.out.println(a);
				}
				else if(ard.compareTo("-sort")==0)
				{
					Arrays.sort(breakfast,new Comparator()
					{
						public int compare(Object f1, Object f2) 
						{
							if (f1==null) return 1;
							if (f2==null) return -1;
							return ((Food)f1).getName().compareTo(((Food)f2).getName());
						};
					});
				}
				continue;
			}
			String[] parts=ard.split("/");
			try{
				Class myclass=  Class.forName("obba." + parts[0]);
				if(parts.length>=4 || parts.length<3){
					throw new Exception();
				}
				else {
					Constructor constr=myclass.getConstructor(String.class,String.class);
					breakfast[i]=(Food)constr.newInstance(parts[1],parts[2]);
				}
			}
			catch(ClassNotFoundException e){
				System.out.println("class not found");
				args[i]=null;
			}
			catch(NoSuchMethodException t){
				System.out.println("constructor not found");
				args[i]=null;
			}
			catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			catch (InstantiationException e) {
				e.printStackTrace();
			} 
			catch (IllegalAccessException e) {
				e.printStackTrace();
			} 
			catch (InvocationTargetException e) {
				e.printStackTrace();
			} 
			catch (Exception e) {
				System.out.println("invalid number of arguments");
				args[i]=null;
				e.printStackTrace();
			}
		}
		String argum=obj.nextLine();
		String[] params=argum.split("/");
		int counter=0;
		Food prover = null;
		try{
			Class myclass=  Class.forName("obba." + params[0]);
			if(params.length>=4 || params.length<3){
				System.out.println("invalid number of arguments");
			}
			else if(params.length==3){
				Constructor constr=myclass.getConstructor(String.class,String.class);
				prover=(Food)constr.newInstance(params[1],params[2]);
			}
		}
		catch(ClassNotFoundException e){
			System.out.println("class not found");
		}
		catch(NoSuchMethodException t){
			System.out.println("constructor not found");
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
		} 
		catch (IllegalAccessException e) {
			e.printStackTrace();
		} 
		catch (InvocationTargetException e) {
			e.printStackTrace();
		} 
		for(Food meal:breakfast)
		{
			if(meal!=null)
			{
				if(meal.equals(prover)==true)
					counter++;
			}
		}
		System.out.println(counter);
	}//close main
}//close class privet

