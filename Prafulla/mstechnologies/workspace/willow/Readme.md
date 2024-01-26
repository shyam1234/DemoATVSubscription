This project has two separate codebase for mobile and tv to meet the single apk goal.
In app, you can find mobile related code under mobile package and for tv, 
you can find under tv package.

For mobile and tv, we don't have separate folders for resources.


For building project for mobile, 
1. Go to edit configuration, select Launch options as nothing
2. Now, Run 'app'

For building project for Tv,
1. Go to edit configuration, select Launch options as nothing
2. Now, Run 'app'

In both case, build will install into device. 

Note: 
1.It won't auto populate in device. Manually, you need to run app in device.
2. In Common files, you can find mobile code stuff under comment like <--for Mobile code-->
3. Need to handle dynamic linking gracefully for mobile
